package com.github.clasicrando.datasources.model

import org.snappy.ksp.symbols.RowParser
import org.snappy.postgresql.type.PgType

@RowParser
@PgType(name = "em.data_source_contacts", arrayType = true)
data class DataSourceContact(
    val contactId: ContactId,
    val dsId: DsId,
    val name: String,
    val email: String?,
    val website: String?,
    val type: String?,
    val notes: String?,
)
