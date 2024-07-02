package com.github.clasicrando.users.model

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID

@JvmInline
@Serializable
value class UserId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()
}

fun UUID.toUserId(): UserId = UserId(this)

@Serializable
data class UserIdJson(
    val userId: UserId,
)
