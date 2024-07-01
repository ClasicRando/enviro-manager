package com.github.clasicrando.users.model

import kotlinx.uuid.UUID

@JvmInline
value class UserId(
    val value: UUID,
) {
    override fun toString(): String = value.toString()
}
