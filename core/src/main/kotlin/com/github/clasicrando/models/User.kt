package com.github.clasicrando.models

import org.snappy.ksp.symbols.RowParser
import java.util.UUID

@RowParser
data class User(
    val userId: UUID,
    val username: String,
    val fullName: String,
    val roles: List<Role>,
) {
    fun hasRole(role: Role): Boolean = roles.any { it == Role.Admin || it == role }
}
