package com.github.clasicrando.users.model

import org.snappy.ksp.symbols.RowParser

@RowParser
data class User(
    val userId: UserId,
    val username: String,
    val fullName: String,
    val roles: List<Role>,
) {
    fun hasRole(role: Role): Boolean = roles.any { it == Role.Admin || it == role }
}
