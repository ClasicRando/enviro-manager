package com.github.clasicrando.datasources.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull

data class RecordWarehouseType(
    val id: RecordWarehouseTypeId,
    val name: String,
    val description: String,
) {
    companion object : RowParser<RecordWarehouseType> {
        override fun fromRow(row: DataRow): RecordWarehouseType =
            RecordWarehouseType(
                id = row.getAsNonNull<Short>("id").toRecordWarehouseTypeId(),
                name = row.getAsNonNull("name"),
                description = row.getAsNonNull("description"),
            )
    }
}
