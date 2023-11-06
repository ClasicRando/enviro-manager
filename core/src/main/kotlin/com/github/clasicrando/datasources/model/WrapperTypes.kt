package com.github.clasicrando.datasources.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ContactId(val value: Long) {
    override fun toString(): String {
        return value.toString()
    }
}

@Serializable
@JvmInline
value class DsId(val value: Long) {
    override fun toString(): String {
        return value.toString()
    }
}

@Serializable
@JvmInline
value class RecordWarehouseTypeId(val value: Short) {
    override fun toString(): String {
        return value.toString()
    }
}
