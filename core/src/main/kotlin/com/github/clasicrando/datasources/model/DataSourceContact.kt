package com.github.clasicrando.datasources.model

import com.github.clasicrando.jasync.symbol.Rename
import com.github.clasicrando.jasync.symbol.ResultRow

@ResultRow
data class DataSourceContact(
    @Rename("contact_id")
    val contactId: ContactId,
    @Rename("ds_id")
    val dsId: DsId,
    val name: String,
    val email: String?,
    val website: String?,
    val type: String?,
    val notes: String?,
)
