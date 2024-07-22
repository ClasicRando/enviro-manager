package com.github.clasicrando.workflows.data.postgres

import com.github.clasicrando.workflows.data.WorkflowsDao
import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowId
import com.github.clasicrando.workflows.model.WorkflowTaskComposite
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.executeClosing
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.intellij.lang.annotations.Language
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
                .createPreparedQuery(GET_WORKFLOWS)
                .fetchAll(Workflow)
        }

    override suspend fun getById(id: WorkflowId): Workflow? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_WORKFLOW)
                .bind(id.value)
                .fetchFirst(Workflow)
        }

    override suspend fun createWorkflow(
        name: String,
        workflowTasks: List<WorkflowTaskComposite>,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(INSERT_WORKFLOW)
                .bind(name)
                .bind(workflowTasks)
                .executeClosing()
        }
    }

    override suspend fun updateWorkflow(
        workflowId: WorkflowId,
        name: String,
        workflowTasks: List<WorkflowTaskComposite>,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(UPDATE_WORKFLOW)
                .bind(name.trim())
                .bind(workflowId.value)
                .bind(workflowTasks)
                .executeClosing()
        }
    }
}

@Language("POSTGRES-PSQL")
private val GET_WORKFLOWS =
    """
    SELECT w.workflow_id, w.name, w.is_deprecated, w.new_workflow
    FROM workflow_engine.workflows w
    ORDER BY w.workflow_id
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val GET_WORKFLOW =
    """
    SELECT w.workflow_id, w.name, w.is_deprecated, w.new_workflow
    FROM workflow_engine.workflows w
    WHERE w.workflow_id = $1
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val INSERT_WORKFLOW =
    """
    WITH new_workflow AS (
        INSERT INTO workflow_engine.workflows(name)
        VALUES($1)
        RETURNING workflow_id
    )
    INSERT INTO workflow_engine.workflow_tasks(workflow_id, task_order, task_id, default_input_parameters)
    SELECT w.workflow_id, t.task_order, t.task_id, t.default_input_parameters
    FROM new_workflow w
    CROSS JOIN UNNEST($2::workflow_engine.workflow_tasks[]) t(workflow_id, task_order, task_id, default_input_parameters)
    """

@Language("POSTGRES-PSQL")
private val UPDATE_WORKFLOW =
    """
    WITH updated_workflow AS (
        UPDATE workflow_engine.workflows
        SET name = $1
        WHERE workflow_id = $2
        RETURNING workflow_id
    ), expanded_tasks AS (
        SELECT et.workflow_id, et.task_order, et.task_id, et.default_input_parameters
        FROM UNNEST($3::workflow_engine.workflow_tasks[]) et(workflow_id, task_order, task_id, default_input_parameters)
    )
    MERGE INTO workflow_engine.workflow_tasks wt
    USING (
        SELECT DISTINCT
            w.workflow_id, et.task_order, et.task_id, et.default_input_parameters, FALSE AS remove_task
        FROM updated_workflow w
        JOIN expanded_tasks et ON w.workflow_id = et.workflow_id
        UNION ALL
        SELECT
            w.workflow_id, wt.task_order, wt.task_id, wt.default_input_parameters, TRUE AS remove_task
        FROM updated_workflow w
        JOIN workflow_engine.workflow_tasks wt ON w.workflow_id = wt.workflow_id
        WHERE NOT EXISTS(
            SELECT NULL
            FROM expanded_tasks et
            WHERE
                et.workflow_id = wt.workflow_id
                AND et.task_order = wt.task_order
        )
    ) nt
    ON (wt.workflow_id = nt.workflow_id AND wt.task_order = nt.task_order)
    WHEN MATCHED AND nt.remove_task THEN
        DELETE
    WHEN MATCHED AND NOT nt.remove_task THEN
        UPDATE SET
            task_id = nt.task_id,
            default_input_parameters = nt.default_input_parameters
    WHEN NOT MATCHED THEN
        INSERT(workflow_id, task_order, task_id, default_input_parameters)
        VALUES (nt.workflow_id, nt.task_order, nt.task_id, nt.default_input_parameters);
    """.trimIndent()
