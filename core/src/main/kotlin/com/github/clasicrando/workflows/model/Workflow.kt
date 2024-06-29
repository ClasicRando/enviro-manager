package com.github.clasicrando.workflows.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull

data class Workflow(
    val id: WorkflowId,
    val name: String,
    val workflowDefinitionName: String,
    val pipelineState: String,
) {
    companion object : RowParser<Workflow> {
        override fun fromRow(row: DataRow): Workflow {
            return Workflow(
                id = WorkflowId(row.getAsNonNull("id")),
                name = row.getAsNonNull("name"),
                workflowDefinitionName = row.getAsNonNull("workflow_definition_name"),
                pipelineState = row.getAsNonNull("pipeline_state"),
            )
        }
    }
}
