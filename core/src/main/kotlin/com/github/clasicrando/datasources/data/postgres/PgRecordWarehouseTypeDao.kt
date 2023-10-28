package com.github.clasicrando.datasources.data.postgres

import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.model.RecordWarehouseType
import com.github.clasicrando.datasources.model.RecordWarehouseTypeId
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.snappy.command.sqlCommand
import org.snappy.extensions.useConnection
import javax.sql.DataSource

class PgRecordWarehouseTypeDao(override val di: DI) : DIAware, RecordWarehouseTypesDao {
    private val dataSource: DataSource by di.instance()

    override suspend fun getAll(): List<RecordWarehouseType> {
        return dataSource.useConnection {
            sqlCommand(
                """
                select rwt.id, rwt.name, rwt.description
                from pipeline.v_record_warehouse_types rwt
                """.trimIndent(),
            )
                .querySuspend<RecordWarehouseType>(this)
                .toList()
        }
    }

    override suspend fun getById(id: RecordWarehouseTypeId): RecordWarehouseType? {
        return dataSource.useConnection {
            sqlCommand(
                """
                select rwt.id, rwt.name, rwt.description
                from pipeline.v_record_warehouse_types rwt
                where rwt.id = ?
                """.trimIndent(),
            )
                .bind(id)
                .querySingleOrNullSuspend<RecordWarehouseType>(this)
        }
    }
}
