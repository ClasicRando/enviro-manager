package com.github.clasicrando.datasources

import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DataSourceWithContacts
import com.github.clasicrando.datasources.model.DsId
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.snappy.command.sqlCommand
import org.snappy.extensions.useConnection

class PgDataSourcesDao(override val di: DI) : DIAware, DataSourcesDao {
    private val dataSource: javax.sql.DataSource by di.instance()

    override suspend fun getById(dsId: DsId): DataSource? {
        return dataSource.useConnection {
            sqlCommand(
                """
                select
                    ds.ds_id, ds.code, ds.prov, ds.country, ds.prov_level, ds.description,
                    ds.files_location, ds.comments, ds.search_radius, ds.reporting_type,
                    ds.record_warehouse_type, ds.assigned_user, ds.created_by, ds.created,
                    ds.updated_by, ds.last_updated, ds.collection_workflow, ds.load_workflow,
                    ds.check_workflow, ds.qa_workflow
                from em.v_data_sources ds
                where ds.ds_id = ?
                """.trimIndent(),
            )
                .bind(dsId)
                .queryFirstOrNullSuspend<DataSource>(this)
        }
    }

    override suspend fun getByIdWithContacts(dsId: DsId): DataSourceWithContacts? {
        return dataSource.useConnection {
            sqlCommand(
                """
                select
                    ds.ds_id, ds.code, ds.prov, ds.country, ds.prov_level, ds.description,
                    ds.files_location, ds.comments, ds.search_radius, ds.reporting_type,
                    ds.record_warehouse_type, ds.assigned_user, ds.created_by, ds.created,
                    ds.updated_by, ds.last_updated, ds.collection_workflow, ds.load_workflow,
                    ds.check_workflow, ds.qa_workflow, ds.contacts
                from em.v_data_sources_with_contacts ds
                where ds.ds_id = ?
                """.trimIndent(),
            )
                .bind(dsId)
                .queryFirstOrNullSuspend<DataSourceWithContacts>(this)
        }
    }

    override suspend fun getAll(): List<DataSource> {
        return dataSource.useConnection {
            sqlCommand(
                """
                select
                    ds.ds_id, ds.code, ds.prov, ds.country, ds.prov_level, ds.description,
                    ds.files_location, ds.comments, ds.search_radius, ds.reporting_type,
                    ds.record_warehouse_type, ds.assigned_user, ds.created_by, ds.created,
                    ds.updated_by, ds.last_updated, ds.collection_workflow, ds.load_workflow,
                    ds.check_workflow, ds.qa_workflow
                from em.v_data_sources ds
                """.trimIndent(),
            )
                .querySuspend<DataSource>(this)
                .toList()
        }
    }
}
