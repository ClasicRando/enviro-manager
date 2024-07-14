package com.github.clasicrando.users.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: UserId,
    val username: String,
    val fullName: String,
    val roles: List<Role>,
    val enabled: Boolean,
) {
    private val allRoles by lazy {
        sequence {
            for (role in roles) {
                yield(role)
                yieldAll(role.inheritedRoles)
            }
        }.sortedBy { it.dbValue }
            .toList()
    }

    fun hasRole(role: Role): Boolean = allRoles.any { it == Role.Admin || it == role }

    companion object : RowParser<User> {
        override fun fromRow(row: DataRow): User =
            User(
                userId = UserId(row.getAsNonNull("user_id")),
                username = row.getAsNonNull("username"),
                fullName = row.getAsNonNull("full_name"),
                roles =
                    row
                        .getAsNonNull<List<String>>("roles")
                        .map { Role.fromString(it) },
                enabled = row.getAsNonNull("enabled"),
            )
    }
}
