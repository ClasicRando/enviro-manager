package com.github.clasicrando.datasources.model

import com.github.clasicrando.jasync.symbol.ResultRow

@ResultRow
data class RecordWarehouseType(
    val id: RecordWarehouseTypeId,
    val name: String,
    val description: String,
)
