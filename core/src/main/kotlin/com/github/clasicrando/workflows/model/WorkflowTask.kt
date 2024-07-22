package com.github.clasicrando.workflows.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAs
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import io.github.clasicrando.kdbc.postgresql.type.PgJson

data class WorkflowTask(
    val workflowId: WorkflowId,
    val taskOrder: Int,
    val taskId: TaskId,
    val name: String,
    val description: String,
    val defaultInputParameters: PgJson?,
) {
    companion object : RowParser<WorkflowTask> {
        override fun fromRow(row: DataRow): WorkflowTask =
            WorkflowTask(
                workflowId = row.getAsNonNull<Int>("workflow_id").toWorkflowId(),
                taskOrder = row.getAsNonNull("task_order"),
                taskId = row.getAsNonNull<Long>("task_id").toTaskId(),
                name = row.getAsNonNull("name"),
                description = row.getAsNonNull("description"),
                defaultInputParameters = row.getAs("default_input_parameters"),
            )
    }
}
