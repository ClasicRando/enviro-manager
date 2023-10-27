package com.github.clasicrando.datasources

import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DataSourceWithContacts
import com.github.clasicrando.datasources.model.DsId

interface DataSourcesDao {
    suspend fun getById(dsId: DsId): DataSource?

    suspend fun getByIdWithContacts(dsId: DsId): DataSourceWithContacts?

    suspend fun getAll(): List<DataSource>
}
