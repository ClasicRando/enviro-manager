package com.github.clasicrando.users.data

import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserId
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.executeClosing
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.core.query.fetchScalar
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import kotlinx.uuid.UUID
import org.intellij.lang.annotations.Language
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
                .createPreparedQuery(GET_BY_ID)
                .bind(userId.value)
                .fetchFirst(User)
        }

    override suspend fun getByUsername(username: String): User? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_BY_USERNAME)
                .bind(username)
                .fetchFirst(User)
        }

    override suspend fun validateUser(loginRequest: LoginRequest): UserId? {
        val rawValue =
            pool.useConnection { conn ->
                conn
                    .createPreparedQuery(VALIDATE_USER)
                    .bind(loginRequest.username)
                    .bind(loginRequest.password)
                    .fetchScalar<UUID>()
            }
        return rawValue?.let { UserId(it) }
    }

    override suspend fun getWithRole(role: Role): List<User> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_USERS_WITH_ROLE)
                .bind(role.dbValue)
                .fetchAll(User)
        }

    override suspend fun getAll(): List<User> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_USERS)
                .fetchAll(User)
        }

    private suspend fun updateUserIsActive(
        userId: UserId,
        isActive: Boolean,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(USER_SET_IS_ACTIVE)
                .bind(isActive)
                .bind(userId.value)
                .executeClosing()
        }
    }

    override suspend fun deactivateUser(userId: UserId) =
        updateUserIsActive(userId = userId, isActive = false)

    override suspend fun activateUser(userId: UserId) =
        updateUserIsActive(userId = userId, isActive = true)

    override suspend fun createUser(
        username: String,
        fullName: String,
        password: String,
        roles: List<Role>,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(CREATE_USER)
                .bind(fullName.trim())
                .bind(username.trim())
                .bind(password)
                .bind(roles.map { it.dbValue })
                .executeClosing()
        }
    }

    override suspend fun updateUser(
        userId: UserId,
        username: String,
        fullName: String,
        roles: List<Role>,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(UPDATE_USER)
                .bind(username.trim())
                .bind(fullName.trim())
                .bind(userId.value)
                .bind(roles)
                .executeClosing()
        }
    }

    override suspend fun resetPassword(
        userId: UserId,
        newPassword: String,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(RESET_PASSWORD)
                .bind(newPassword)
                .bind(userId.value)
                .executeClosing()
        }
    }
}

@Language("POSTGRES-PSQL")
private val GET_BY_ID =
    """
    SELECT u.user_id, u.username, u.full_name, u.enabled, u.roles
    FROM em.v_users u
    WHERE u.user_id = $1
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val GET_BY_USERNAME =
    """
    SELECT u.user_id, u.username, u.full_name, u.enabled, u.roles
    FROM em.v_users u
    WHERE u.username = $1
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val VALIDATE_USER =
    """
    SELECT u2.user_id
    FROM em.users u2
    WHERE
        u2.username = $1
        AND u2.password = CRYPT($2, u2.password)
        AND u2.active
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val GET_USERS_WITH_ROLE =
    """
    SELECT u.user_id, u.username, u.full_name, u.enabled, u.roles
    FROM em.v_users u
    WHERE
        $1 = ANY(u.roles)
        OR 'admin' = ANY(u.roles)
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val GET_USERS =
    """
    SELECT u.user_id, u.username, u.full_name, u.enabled, u.roles
    FROM em.v_users u
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val USER_SET_IS_ACTIVE =
    """
    UPDATE em.users u
    SET active = $1
    WHERE u.user_id = $2
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val CREATE_USER =
    """
    WITH new_user AS (
        INSERT INTO em.users AS u (full_name, username, password)
        VALUES($1, $2, crypt($3, gen_salt('bf')))
        RETURNING u.user_id
    )
    INSERT INTO em.user_roles(user_id, role)
    SELECT u.user_id, r.role
    FROM new_user u
    CROSS JOIN UNNEST($4) r(role)
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val RESET_PASSWORD =
    """
    UPDATE em.users u
    SET password = crypt($1, gen_salt('bf'))
    WHERE u.user_id = $2
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val UPDATE_USER =
    """
    WITH updated_user AS (
        UPDATE em.users u
        SET
            username = $1,
            full_name = $2
        WHERE u.user_id = $3
    )
    MERGE INTO em.user_roles AS u
    USING (
        SELECT
            u.user_id, COALESCE(ur.role, r.description) AS role,
            r.description IS NULL AS revoke_role
        FROM updated_user u
        LEFT JOIN em.user_roles ur ON u.user_id = ur.user_id
        LEFT JOIN LATERAL UNNEST($4::text[]) r(description) ON TRUE
        WHERE COALESCE(ur.role, r.description) IS NOT NULL
    ) t
    ON (u.user_id = t.user_id AND u.role = t.role)
    WHEN MATCHED AND t.revoke_role THEN
        DELETE
    WHEN MATCHED AND NOT t.revoke_role THEN
        DO NOTHING
    WHEN NOT MATCHED THEN
        INSERT(user_id, role)
        VALUES(t.user_id, t.role);
    """.trimIndent()
