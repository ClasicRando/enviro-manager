package com.github.clasicrando.users.model

import com.github.clasicrando.jasync.symbol.FromString
import com.github.clasicrando.jasync.symbol.Rename
import com.github.clasicrando.jasync.symbol.ResultRow

@ResultRow
data class User(
    @Rename("user_id")
    val userId: UserId,
    val username: String,
    @Rename("full_name")
    val fullName: String,
    @FromString(parseMethod = "fromString")
    val roles: List<Role>,
) {
    fun hasRole(role: Role): Boolean = roles.any { it == Role.Admin || it == role }
}
