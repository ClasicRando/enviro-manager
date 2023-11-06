package com.github.clasicrando.workflows.model

import com.github.clasicrando.jasync.symbol.Rename
import com.github.clasicrando.jasync.symbol.ResultRow

@ResultRow
data class Workflow(
    val id: WorkflowId,
    val name: String,
    @Rename("workflow_definition_name")
    val workflowDefinitionName: String,
    @Rename("pipeline_state")
    val pipelineState: String,
)
