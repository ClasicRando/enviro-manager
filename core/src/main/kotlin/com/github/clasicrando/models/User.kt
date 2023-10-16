package com.github.clasicrando.models

import java.util.UUID

data class User(
    val userId: UUID,
    val username: String,
    val fullName: String,
    val roles: List<String>,
)
