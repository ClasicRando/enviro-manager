package com.github.clasicrando.workflows.data

import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowId
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.snappy.command.sqlCommand
import org.snappy.extensions.useConnection
import javax.sql.DataSource

class PgWorkflowsDao(override val di: DI) : DIAware, WorkflowsDao {
    private val dataSource: DataSource by di.instance()

    override suspend fun getAll(): List<Workflow> {
        return dataSource.useConnection {
            sqlCommand(
                """
                select w.id, w.name, w.workflow_definition_name, w.pipeline_state
                from pipeline.v_workflows w
                """.trimIndent(),
            )
                .querySuspend<Workflow>(this)
                .toList()
        }
    }

    override suspend fun getById(id: WorkflowId): Workflow? {
        return dataSource.useConnection {
            sqlCommand(
                """
                select w.id, w.name, w.workflow_definition_name, w.pipeline_state
                from pipeline.v_workflows w
                where w.id = ?
                """.trimIndent(),
            )
                .bind(id)
                .querySingleOrNullSuspend<Workflow>(this)
        }
    }
}
