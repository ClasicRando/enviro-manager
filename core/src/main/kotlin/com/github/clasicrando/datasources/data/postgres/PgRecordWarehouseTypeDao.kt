package com.github.clasicrando.datasources.data.postgres

import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.model.RecordWarehouseType
import com.github.clasicrando.datasources.model.RecordWarehouseTypeId
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.postgresql.connection.PgAsyncConnection
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgRecordWarehouseTypeDao(override val di: DI) : DIAware, RecordWarehouseTypesDao {
    private val connection: PgAsyncConnection by di.instance()

    override suspend fun getAll(): List<RecordWarehouseType> {
        return connection.createPreparedQuery(
            """
            select rwt.id, rwt.name, rwt.description
            from pipeline.v_record_warehouse_types rwt
            """.trimIndent(),
        )
            .fetchAll(RecordWarehouseType)
    }

    override suspend fun getById(id: RecordWarehouseTypeId): RecordWarehouseType? {
        return connection.createPreparedQuery(
            """
            select rwt.id, rwt.name, rwt.description
            from pipeline.v_record_warehouse_types rwt
            where rwt.id = $1
            """.trimIndent(),
        )
            .bind(id.value)
            .fetchFirst(RecordWarehouseType)
    }
}
