package com.github.clasicrando.datasources.data.postgres

import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DataSourceWithContacts
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.jasync.query.sqlCommand
import com.github.clasicrando.requests.UpdateDateSourceRequest
import com.github.clasicrando.users.model.UserId
import com.github.jasync.sql.db.Connection
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgDataSourcesDao(override val di: DI) : DIAware, DataSourcesDao {
    private val connection: Connection by di.instance()

    override suspend fun getById(dsId: DsId): DataSource? {
        return sqlCommand(
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
            .queryFirstOrNull<DataSource>(connection)
    }

    override suspend fun getByIdWithContacts(dsId: DsId): DataSourceWithContacts? {
        return sqlCommand(
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
            .queryFirstOrNull<DataSourceWithContacts>(connection)
    }

    override suspend fun getAll(): List<DataSource> {
        return sqlCommand(
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
            .query<DataSource>(connection)
    }

    override suspend fun update(
        currentUser: UserId,
        dsId: DsId,
        request: UpdateDateSourceRequest,
    ) {
        sqlCommand("call em.update_data_source(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            .bind(dsId)
            .bind(request.description)
            .bind(request.filesLocation)
            .bind(request.comments.takeIf { it.isNotBlank() })
            .bind(request.assignedUser)
            .bind(currentUser)
            .bind(request.searchRadius)
            .bind(request.recordWarehouseTypeId)
            .bind(request.reportingType)
            .bind(request.collectionWorkflowId)
            .bind(request.loadWorkflowId)
            .bind(request.checkWorkflowId)
            .bind(request.qaWorkflowId)
            .execute(connection)
    }
}
