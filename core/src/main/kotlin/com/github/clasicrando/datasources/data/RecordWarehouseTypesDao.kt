package com.github.clasicrando.datasources.data

import com.github.clasicrando.datasources.model.RecordWarehouseType
import com.github.clasicrando.datasources.model.RecordWarehouseTypeId

interface RecordWarehouseTypesDao {
    suspend fun getAll(): List<RecordWarehouseType>

    suspend fun getById(id: RecordWarehouseTypeId): RecordWarehouseType?
}
