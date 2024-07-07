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
                    select u.user_id, u.username, u.full_name, u.enabled, u.roles
                    from em.v_users u
                    where u.user_id = $1
                    """.trimIndent(),
                ).bind(userId.value)
                .fetchFirst(User)
        }

    override suspend fun getByUsername(username: String): User? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    select u.user_id, u.username, u.full_name, u.enabled, u.roles
                    from em.v_users u
                    where u.username = $1
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
                        select u2.user_id
                        from em.users u2
                        where
                            u2.username = $1
                            and u2.password = crypt($2, u2.password)
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
                    select u.user_id, u.username, u.full_name, u.enabled, u.roles
                    from em.v_users u
                    where
                        $1 = any(u.roles)
                        or 'admin' = any(u.roles)
                    """.trimIndent(),
                ).bind(role.dbValue)
                .fetchAll(User)
        }

    override suspend fun getAll(): List<User> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    select u.user_id, u.username, u.full_name, u.enabled, u.roles
                    from em.v_users u
                    """.trimIndent(),
                ).fetchAll(User)
        }

    private suspend fun updateUserIsEnabled(
        userId: UserId,
        isEnabled: Boolean,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    update em.users u
                    set enabled = $1
                    where u.user_id = $2
                    """.trimIndent(),
                ).bind(isEnabled)
                .bind(userId.value)
                .executeClosing()
        }
    }

    override suspend fun disableUser(userId: UserId) =
        updateUserIsEnabled(userId = userId, isEnabled = false)

    override suspend fun enableUser(userId: UserId) =
        updateUserIsEnabled(userId = userId, isEnabled = true)

    override suspend fun modifyRoles(
        userId: UserId,
        roles: List<Role>,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    merge into em.user_roles as u
                    using (
                        select
                            u.user_id, coalesce(ur.role, r.description) role,
                            r.description IS NULL as revoke_role
                        from em.users u
                        left join em.user_roles ur on u.user_id = ur.user_id
                        left join lateral unnest($1::text[]) r(description) on true
                        where u.user_id = $2
                    ) t
                    on (u.user_id = t.user_id and u.role = t.role)
                    when matched and t.revoke_role then
                        delete
                    when matched and not t.revoke_role then
                        do nothing
                    when not matched then
                        insert(user_id, role)
                        values(t.user_id, t.role);
                    """.trimIndent(),
                ).bind(roles.map { it.dbValue })
                .bind(userId.value)
                .executeClosing()
        }
    }

    override suspend fun updateUser(
        userId: UserId,
        username: String,
        fullName: String,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    update em.users u
                    set
                        username = trim($1),
                        full_name = trim($2)
                    where u.user_id = $3
                    """.trimIndent(),
                ).bind(username)
                .bind(fullName)
                .bind(userId.value)
                .executeClosing()
        }
    }
}
