package com.github.clasicrando.datasources.data.postgres

import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.model.RecordWarehouseType
import com.github.clasicrando.datasources.model.RecordWarehouseTypeId
import com.github.clasicrando.jasync.query.sqlCommand
import com.github.jasync.sql.db.Connection
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgRecordWarehouseTypeDao(override val di: DI) : DIAware, RecordWarehouseTypesDao {
    private val connection: Connection by di.instance()

    override suspend fun getAll(): List<RecordWarehouseType> {
        return sqlCommand(
            """
            select rwt.id, rwt.name, rwt.description
            from pipeline.v_record_warehouse_types rwt
            """.trimIndent(),
        )
            .query<RecordWarehouseType>(connection)
    }

    override suspend fun getById(id: RecordWarehouseTypeId): RecordWarehouseType? {
        return sqlCommand(
            """
            select rwt.id, rwt.name, rwt.description
            from pipeline.v_record_warehouse_types rwt
            where rwt.id = ?
            """.trimIndent(),
        )
            .bind(id)
            .querySingleOrNull<RecordWarehouseType>(connection)
    }
}
