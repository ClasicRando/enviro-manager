package com.github.clasicrando.datasources.model

import org.snappy.ksp.symbols.Flatten
import org.snappy.ksp.symbols.RowParser

@RowParser
data class DataSourceWithContacts(
    @Flatten
    val dataSource: DataSource,
    val contacts: List<DataSourceContact>?,
)
