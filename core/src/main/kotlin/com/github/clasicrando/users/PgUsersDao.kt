package com.github.clasicrando.users

import com.github.clasicrando.requests.LoginRequest
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
                .queryFirstOrNullSuspend<User>(this)
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
}
