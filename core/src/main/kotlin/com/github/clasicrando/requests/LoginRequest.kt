package com.github.clasicrando.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
) : ApiRequest {
    override fun validate(): String? =
        when {
            username.isBlank() -> "Username cannot be blank"
            password.isBlank() -> "Password cannot be blank"
            else -> null
        }
}
