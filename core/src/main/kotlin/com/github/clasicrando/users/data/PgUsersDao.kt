package com.github.clasicrando.users.data

import com.github.clasicrando.jasync.query.sqlCommand
import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserId
import com.github.jasync.sql.db.Connection
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import java.util.UUID

class PgUsersDao(override val di: DI) : DIAware, UsersDao {
    private val connection: Connection by di.instance()

    override suspend fun getById(userId: UserId): User? {
        return sqlCommand(
            """
            select u.user_id, u.username, u.full_name, u.roles
            from em.v_users u
            where u.user_id = ?
            """.trimIndent(),
        )
            .bind(userId)
            .querySingleOrNull<User>(connection)
    }

    override suspend fun getByUsername(username: String): User? {
        return sqlCommand(
            """
            select u.user_id, u.username, u.full_name, u.roles
            from em.v_users u
            where u.username = ?
            """.trimIndent(),
        )
            .bind(username)
            .querySingleOrNull<User>(connection)
    }

    override suspend fun validateUser(loginRequest: LoginRequest): UserId? {
        val rawValue =
            sqlCommand("select em.validate_user(?, ?)")
                .bind(loginRequest.username)
                .bind(loginRequest.password)
                .queryScalarOrNull<UUID>(connection)
        return rawValue?.let { UserId(it) }
    }

    override suspend fun getWithRole(role: Role): List<User> {
        return sqlCommand(
            """
            select u.user_id, u.username, u.full_name, u.roles
            from em.v_users u
            where
                ? = any(u.roles)
                or 'admin' = any(u.roles)
            """.trimIndent(),
        )
            .bind(role.dbValue)
            .query<User>(connection)
    }
}
