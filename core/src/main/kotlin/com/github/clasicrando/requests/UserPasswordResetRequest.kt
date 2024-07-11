package com.github.clasicrando.requests

import com.github.clasicrando.users.model.UserId
import kotlinx.serialization.Serializable

@Serializable
data class UserPasswordResetRequest(
    val modalId: String,
    val userId: UserId,
    val password: String,
    val confirmPassword: String,
) : ApiRequest {
    override fun validate(): String? {
        if (password == confirmPassword) {
            return null
        }
        return "Passwords must match"
    }
}
