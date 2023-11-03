package com.github.clasicrando.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)
