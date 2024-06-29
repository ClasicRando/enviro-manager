package com.github.clasicrando.users.data

import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserId
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.core.query.fetchScalar
import io.github.clasicrando.kdbc.postgresql.connection.PgAsyncConnection
import kotlinx.uuid.UUID
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgUsersDao(override val di: DI) : DIAware, UsersDao {
    private val connection: PgAsyncConnection by di.instance()

    override suspend fun getById(userId: UserId): User? {
        return connection.createPreparedQuery(
            """
            select u.user_id, u.username, u.full_name, u.roles
            from em.v_users u
            where u.user_id = $1
            """.trimIndent(),
        )
            .bind(userId.value)
            .fetchFirst(User)
    }

    override suspend fun getByUsername(username: String): User? {
        return connection.createPreparedQuery(
            """
            select u.user_id, u.username, u.full_name, u.roles
            from em.v_users u
            where u.username = $1
            """.trimIndent(),
        )
            .bind(username)
            .fetchFirst(User)
    }

    override suspend fun validateUser(loginRequest: LoginRequest): UserId? {
        val rawValue =
            connection.createPreparedQuery("select em.validate_user($1, $2)")
                .bind(loginRequest.username)
                .bind(loginRequest.password)
                .fetchScalar<UUID>()
        return rawValue?.let { UserId(it) }
    }

    override suspend fun getWithRole(role: Role): List<User> {
        return connection.createPreparedQuery(
            """
            select u.user_id, u.username, u.full_name, u.roles
            from em.v_users u
            where
                $1 = any(u.roles)
                or 'admin' = any(u.roles)
            """.trimIndent(),
        )
            .bind(role.dbValue)
            .fetchAll(User)
    }
}
