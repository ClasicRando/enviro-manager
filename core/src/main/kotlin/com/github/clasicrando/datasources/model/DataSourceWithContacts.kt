package com.github.clasicrando.datasources.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAs

data class DataSourceWithContacts(
    val dataSource: DataSource,
    val contacts: List<DataSourceContact>?,
) {
    companion object : RowParser<DataSourceWithContacts> {
        override fun fromRow(row: DataRow): DataSourceWithContacts =
            DataSourceWithContacts(
                dataSource = DataSource.fromRow(row),
                contacts = row.getAs("contacts"),
            )
    }
}
