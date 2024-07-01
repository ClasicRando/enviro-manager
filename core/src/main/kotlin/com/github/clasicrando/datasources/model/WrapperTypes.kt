package com.github.clasicrando.datasources.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ContactId(
    val value: Long,
) {
    override fun toString(): String = value.toString()
}

@Serializable
@JvmInline
value class DsId(
    val value: Long,
) {
    override fun toString(): String = value.toString()
}

@Serializable
@JvmInline
value class RecordWarehouseTypeId(
    val value: Short,
) {
    override fun toString(): String = value.toString()
}

fun Long.toContactId(): ContactId = ContactId(this)

fun Long.toDsId(): DsId = DsId(this)

fun Short.toRecordWarehouseTypeId(): RecordWarehouseTypeId = RecordWarehouseTypeId(this)
