package com.github.clasicrando.workflows.data

import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowId
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.executeClosing
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

    override suspend fun createWorkflow(
        name: String,
        workflowDefinitionName: String,
        pipelineState: String,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    INSERT INTO pipeline.workflows(name, workflow_definition_name, pipeline_state)
                    VALUES($1, $2, $3)
                    """.trimIndent(),
                ).bind(name)
                .bind(workflowDefinitionName)
                .bind(pipelineState)
                .executeClosing()
        }
    }

    override suspend fun updateWorkflow(
        workflowId: WorkflowId,
        name: String,
        workflowDefinitionName: String,
        pipelineState: String,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    UPDATE pipeline.workflows
                    SET
                        name = $1,
                        workflow_definition_name = $2,
                        pipeline_state = $3
                    WHERE id = $4
                    """.trimIndent(),
                ).bind(name.trim())
                .bind(workflowDefinitionName.trim())
                .bind(pipelineState.trim())
                .bind(workflowId.value)
                .executeClosing()
        }
    }
}
