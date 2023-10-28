package com.github.clasicrando.datasources.model

import org.snappy.ksp.symbols.RowParser

@RowParser
data class RecordWarehouseType(
    val id: RecordWarehouseTypeId,
    val name: String,
    val description: String,
)
