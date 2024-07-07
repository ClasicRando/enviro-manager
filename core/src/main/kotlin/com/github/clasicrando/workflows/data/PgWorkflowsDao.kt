package com.github.clasicrando.workflows.data

import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowId
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgWorkflowsDao(
    override val di: DI,
) : DIAware,
    WorkflowsDao {
    private val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun getAll(): List<Workflow> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    select w.id, w.name, w.workflow_definition_name, w.pipeline_state
                    from pipeline.v_workflows w
                    """.trimIndent(),
                ).fetchAll(Workflow)
        }

    override suspend fun getById(id: WorkflowId): Workflow? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    select w.id, w.name, w.workflow_definition_name, w.pipeline_state
                    from pipeline.v_workflows w
                    where w.id = $1
                    """.trimIndent(),
                ).bind(id.value)
                .fetchFirst(Workflow)
        }
}
