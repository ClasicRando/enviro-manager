package com.github.clasicrando.workflows.data.postgres

import com.github.clasicrando.workflows.data.WorkflowTasksDao
import com.github.clasicrando.workflows.model.WorkflowId
import com.github.clasicrando.workflows.model.WorkflowTask
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.intellij.lang.annotations.Language
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgWorkflowTasksDao(
    override val di: DI,
) : DIAware,
    WorkflowTasksDao {
    private val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun getWorkflowTasks(workflowId: WorkflowId): List<WorkflowTask> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_WORKFLOW_TASKS)
                .bind(workflowId.value)
                .fetchAll(WorkflowTask)
        }
}

@Language("POSTGRES-PSQL")
private val GET_WORKFLOW_TASKS =
    """
    SELECT
        wt.workflow_id, wt.task_order, wt.task_id, wt.name, wt.description,
        wt.default_input_parameters
    FROM workflow_engine.v_workflow_tasks wt
    WHERE wt.workflow_id = $1
    ORDER BY wt.task_order
    """.trimIndent()
