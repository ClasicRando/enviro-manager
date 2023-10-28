package com.github.clasicrando.users.data

import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserId
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.snappy.command.sqlCommand
import org.snappy.extensions.useConnection
import javax.sql.DataSource

class PgUsersDao(override val di: DI) : DIAware, UsersDao {
    private val dataSource: DataSource by di.instance()

    override suspend fun getById(userId: UserId): User? {
        return dataSource.useConnection {
            sqlCommand(
                """
                select u.user_id, u.username, u.full_name, u.roles
                from em.v_users u
                where u.user_id = ?
                """.trimIndent(),
            )
                .bind(userId)
                .querySingleOrNullSuspend<User>(this)
        }
    }

    override suspend fun getByUsername(username: String): User? {
        return dataSource.useConnection {
            sqlCommand(
                """
                select u.user_id, u.username, u.full_name, u.roles
                from em.v_users u
                where u.username = ?
                """.trimIndent(),
            )
                .bind(username)
                .querySingleOrNullSuspend<User>(this)
        }
    }

    override suspend fun validateUser(loginRequest: LoginRequest): UserId? {
        return dataSource.useConnection {
            sqlCommand("select em.validate_user(?, ?)")
                .bind(loginRequest.username)
                .bind(loginRequest.password)
                .queryScalarOrNullSuspend<UserId>(this)
        }
    }

    override suspend fun getWithRole(role: Role): List<User> {
        return dataSource.useConnection {
            sqlCommand(
                """
                select u.user_id, u.username, u.full_name, u.roles
                from em.v_users u
                where
                    ? = any(u.roles)
                    or 'admin' = any(u.roles)
                """.trimIndent(),
            )
                .bind(role.dbValue)
                .querySuspend<User>(this)
                .toList()
        }
    }
}
