package com.github.clasicrando.users.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull

data class User(
    val userId: UserId,
    val username: String,
    val fullName: String,
    val roles: List<Role>,
) {
    fun hasRole(role: Role): Boolean = roles.any { it == Role.Admin || it == role }

    companion object : RowParser<User> {
        override fun fromRow(row: DataRow): User {
            return User(
                userId = UserId(row.getAsNonNull("user_id")),
                username = row.getAsNonNull("username"),
                fullName = row.getAsNonNull("full_name"),
                roles = row.getAsNonNull("roles"),
            )
        }
    }
}
