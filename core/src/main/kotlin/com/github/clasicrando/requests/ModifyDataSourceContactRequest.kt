package com.github.clasicrando.requests

import kotlinx.serialization.Serializable

@Serializable
data class ModifyDataSourceContactRequest(
    val name: String,
    val email: String,
    val website: String,
    val type: String,
    val notes: String,
)
