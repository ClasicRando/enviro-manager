package com.github.clasicrando.datasources.data

import com.github.clasicrando.datasources.model.ContactId
import com.github.clasicrando.datasources.model.DataSourceContact
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.requests.ModifyDataSourceContactRequest

interface DataSourceContactsDao {
    suspend fun getById(contactId: ContactId): DataSourceContact?

    suspend fun getByDsId(dsId: DsId): List<DataSourceContact>

    suspend fun create(
        dsId: DsId,
        request: ModifyDataSourceContactRequest,
    )

    suspend fun update(
        contactId: ContactId,
        dsId: DsId,
        request: ModifyDataSourceContactRequest,
    )

    suspend fun delete(contactId: ContactId, dsId: DsId)
}
