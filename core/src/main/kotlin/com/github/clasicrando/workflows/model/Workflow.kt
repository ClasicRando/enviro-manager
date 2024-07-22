package com.github.clasicrando.workflows.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAs
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class WorkflowId(
    val value: Int,
) {
    override fun toString(): String = value.toString()
}

fun Int.toWorkflowId() = WorkflowId(this)

@Serializable
data class WorkflowIdJson(
    val workflowId: WorkflowId,
)

data class Workflow(
    val workflowId: WorkflowId,
    val name: String,
    val isDeprecated: Boolean,
    val newWorkflow: WorkflowId?,
) {
    companion object : RowParser<Workflow> {
        override fun fromRow(row: DataRow): Workflow =
            Workflow(
                workflowId = row.getAsNonNull<Int>("workflow_id").toWorkflowId(),
                name = row.getAsNonNull("name"),
                isDeprecated = row.getAsNonNull("is_deprecated"),
                newWorkflow = row.getAs<Int>("new_workflow")?.toWorkflowId(),
            )
    }
}
