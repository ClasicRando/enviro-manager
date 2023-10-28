package com.github.clasicrando.datasources.data

import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DataSourceWithContacts
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.requests.UpdateDateSourceRequest
import com.github.clasicrando.users.model.UserId

interface DataSourcesDao {
    suspend fun getById(dsId: DsId): DataSource?

    suspend fun getByIdWithContacts(dsId: DsId): DataSourceWithContacts?

    suspend fun getAll(): List<DataSource>

    suspend fun update(currentUser: UserId, dsId: DsId, request: UpdateDateSourceRequest)
}
