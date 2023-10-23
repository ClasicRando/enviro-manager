package com.github.clasicrando.models

import org.snappy.postgresql.type.PgType

@PgType(name = "em.data_source_contacts", arrayType = true)
data class DataSourceContact(
    val contactId: Long,
    val dsId: Long,
    val name: String,
    val email: String?,
    val website: String?,
    val type: String?,
    val notes: String?,
)
