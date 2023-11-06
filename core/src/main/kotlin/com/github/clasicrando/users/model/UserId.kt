package com.github.clasicrando.users.model

import java.util.UUID

@JvmInline
value class UserId(val value: UUID) {
    override fun toString(): String {
        return value.toString()
    }
}
