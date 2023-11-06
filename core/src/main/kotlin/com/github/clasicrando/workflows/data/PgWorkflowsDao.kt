package com.github.clasicrando.workflows.data

import com.github.clasicrando.jasync.query.sqlCommand
import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowId
import com.github.jasync.sql.db.Connection
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgWorkflowsDao(override val di: DI) : DIAware, WorkflowsDao {
    private val connection: Connection by di.instance()

    override suspend fun getAll(): List<Workflow> {
        return sqlCommand(
            """
            select w.id, w.name, w.workflow_definition_name, w.pipeline_state
            from pipeline.v_workflows w
            """.trimIndent(),
        )
            .query<Workflow>(connection)
    }

    override suspend fun getById(id: WorkflowId): Workflow? {
        return sqlCommand(
            """
            select w.id, w.name, w.workflow_definition_name, w.pipeline_state
            from pipeline.v_workflows w
            where w.id = ?
            """.trimIndent(),
        )
            .bind(id)
            .querySingleOrNull<Workflow>(connection)
    }
}
