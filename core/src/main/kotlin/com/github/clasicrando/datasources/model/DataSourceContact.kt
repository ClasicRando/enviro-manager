package com.github.clasicrando.datasources.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAs
import io.github.clasicrando.kdbc.core.result.getAsNonNull

data class DataSourceContact(
    val contactId: ContactId,
    val dsId: DsId,
    val name: String,
    val email: String?,
    val website: String?,
    val type: String?,
    val notes: String?,
) {
    companion object : RowParser<DataSourceContact> {
        override fun fromRow(row: DataRow): DataSourceContact =
            DataSourceContact(
                contactId = row.getAsNonNull<Long>("contact_id").toContactId(),
                dsId = row.getAsNonNull<Long>("ds_id").toDsId(),
                name = row.getAsNonNull("name"),
                email = row.getAs("email"),
                website = row.getAs("website"),
                type = row.getAs("type"),
                notes = row.getAs("notes"),
            )
    }
}
