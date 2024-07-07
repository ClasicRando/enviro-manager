package com.github.clasicrando.datasources.data.postgres

import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.requests.UpdateDateSourceRequest
import com.github.clasicrando.users.model.UserId
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.executeClosing
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgDataSourcesDao(
    override val di: DI,
) : DIAware,
    DataSourcesDao {
    private val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun getById(dsId: DsId): DataSource? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    select
                        ds.ds_id, ds.code, ds.prov, ds.country, ds.prov_level, ds.description,
                        ds.files_location, ds.comments, ds.search_radius, ds.reporting_type,
                        ds.record_warehouse_type, ds.assigned_user, ds.created_by, ds.created,
                        ds.updated_by, ds.last_updated, ds.collection_workflow, ds.load_workflow,
                        ds.check_workflow, ds.qa_workflow
                    from em.v_data_sources ds
                    where ds.ds_id = $1
                    """.trimIndent(),
                ).bind(dsId.value)
                .fetchFirst(DataSource)
        }

    override suspend fun getAll(): List<DataSource> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    select
                        ds.ds_id, ds.code, ds.prov, ds.country, ds.prov_level, ds.description,
                        ds.files_location, ds.comments, ds.search_radius, ds.reporting_type,
                        ds.record_warehouse_type, ds.assigned_user, ds.created_by, ds.created,
                        ds.updated_by, ds.last_updated, ds.collection_workflow, ds.load_workflow,
                        ds.check_workflow, ds.qa_workflow
                    from em.v_data_sources ds
                    """.trimIndent(),
                ).fetchAll(DataSource)
        }

    override suspend fun update(
        currentUser: UserId,
        dsId: DsId,
        request: UpdateDateSourceRequest,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    update em.data_sources
                    set
                        description = $2,
                        files_location = $3,
                        comments = case when trim(coalesce($4,'')) = '' then null else $4 end,
                        assigned_user = (select u.user_id from em.users u where u.username = $5),
                        last_updated = timezone('utc'::text, now()),
                        updated_by = $6,
                        search_radius = $7,
                        record_warehouse_type = $8,
                        reporting_type = $9,
                        collection_workflow = $10,
                        load_workflow = $11,
                        check_workflow = $12,
                        qa_workflow = $13
                    where ds_id = $1
                    """.trimIndent(),
                ).bind(dsId.value)
                .bind(request.description)
                .bind(request.filesLocation)
                .bind(request.comments.takeIf { it.isNotBlank() })
                .bind(request.assignedUser)
                .bind(currentUser.value)
                .bind(request.searchRadius)
                .bind(request.recordWarehouseTypeId)
                .bind(request.reportingType)
                .bind(request.collectionWorkflowId)
                .bind(request.loadWorkflowId)
                .bind(request.checkWorkflowId)
                .bind(request.qaWorkflowId)
                .executeClosing()
        }
    }
}
