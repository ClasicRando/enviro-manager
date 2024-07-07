package com.github.clasicrando.users.data

import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserId
import io.github.clasicrando.kdbc.core.connection.AsyncConnection
import io.github.clasicrando.kdbc.core.connection.transaction
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.executeClosing
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.core.query.fetchScalar
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import kotlinx.uuid.UUID
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgUsersDao(
    override val di: DI,
) : DIAware,
    UsersDao {
    private val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun getById(userId: UserId): User? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    SELECT u.user_id, u.username, u.full_name, u.enabled, u.roles
                    FROM em.v_users u
                    WHERE u.user_id = $1
                    """.trimIndent(),
                ).bind(userId.value)
                .fetchFirst(User)
        }

    override suspend fun getByUsername(username: String): User? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    SELECT u.user_id, u.username, u.full_name, u.enabled, u.roles
                    FROM em.v_users u
                    WHERE u.username = $1
                    """.trimIndent(),
                ).bind(username)
                .fetchFirst(User)
        }

    override suspend fun validateUser(loginRequest: LoginRequest): UserId? {
        val rawValue =
            pool.useConnection { conn ->
                conn
                    .createPreparedQuery(
                        """
                        SELECT u2.user_id
                        FROM em.users u2
                        WHERE
                            u2.username = $1
                            AND u2.password = CRYPT($2, u2.password)
                        """.trimIndent(),
                    ).bind(loginRequest.username)
                    .bind(loginRequest.password)
                    .fetchScalar<UUID>()
            }
        return rawValue?.let { UserId(it) }
    }

    override suspend fun getWithRole(role: Role): List<User> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    SELECT u.user_id, u.username, u.full_name, u.enabled, u.roles
                    FROM em.v_users u
                    WHERE
                        $1 = ANY(u.roles)
                        OR 'admin' = ANY(u.roles)
                    """.trimIndent(),
                ).bind(role.dbValue)
                .fetchAll(User)
        }

    override suspend fun getAll(): List<User> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    SELECT u.user_id, u.username, u.full_name, u.enabled, u.roles
                    FROM em.v_users u
                    """.trimIndent(),
                ).fetchAll(User)
        }

    private suspend fun updateUserIsActive(
        userId: UserId,
        isActive: Boolean,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    UPDATE em.users u
                    SET active = $1
                    WHERE u.user_id = $2
                    """.trimIndent(),
                ).bind(isActive)
                .bind(userId.value)
                .executeClosing()
        }
    }

    override suspend fun deactivateUser(userId: UserId) =
        updateUserIsActive(userId = userId, isActive = false)

    override suspend fun activateUser(userId: UserId) =
        updateUserIsActive(userId = userId, isActive = true)

    override suspend fun updateUser(
        userId: UserId,
        username: String,
        fullName: String,
        roles: List<Role>,
    ) {
        pool.useConnection { conn ->
            conn.transaction {
                it.updateUserDetails(userId = userId, username = username, fullName = fullName)
                it.modifyRoles(userId = userId, roles = roles)
            }
        }
    }

    private suspend fun AsyncConnection.updateUserDetails(
        userId: UserId,
        username: String,
        fullName: String,
    ) {
        createPreparedQuery(
            """
            UPDATE em.users u
            SET
                username = TRIM($1),
                full_name = TRIM($2)
            WHERE u.user_id = $3
            """.trimIndent(),
        ).bind(username)
            .bind(fullName)
            .bind(userId.value)
            .executeClosing()
    }

    private suspend fun AsyncConnection.modifyRoles(
        userId: UserId,
        roles: List<Role>,
    ) {
        val newRoles = if (roles.any { it == Role.Admin }) listOf(Role.Admin) else roles
        createPreparedQuery(
            """
            MERGE INTO em.user_roles AS u
            USING (
                SELECT
                    u.user_id, COALESCE(ur.role, r.description) AS role,
                    r.description IS NULL AS revoke_role
                FROM em.users u
                LEFT JOIN em.user_roles ur ON u.user_id = ur.user_id
                LEFT JOIN LATERAL UNNEST($1::text[]) r(description) ON TRUE
                WHERE
                    u.user_id = $2
                    AND COALESCE(ur.role, r.description) IS NOT NULL
            ) t
            ON (u.user_id = t.user_id AND u.role = t.role)
            WHEN MATCHED AND t.revoke_role THEN
                DELETE
            WHEN MATCHED AND NOT t.revoke_role THEN
                DO NOTHING
            WHEN NOT MATCHED THEN
                INSERT(user_id, role)
                VALUES(t.user_id, t.role);
            """.trimIndent(),
        ).bind(newRoles.map { it.dbValue })
            .bind(userId.value)
            .executeClosing()
    }
}
