package com.github.clasicrando.datasources.model

import com.github.clasicrando.jasync.symbol.Flatten
import com.github.clasicrando.jasync.symbol.ResultRow

@ResultRow
data class DataSourceWithContacts(
    @Flatten
    val dataSource: DataSource,
    val contacts: List<DataSourceContact>?,
)
