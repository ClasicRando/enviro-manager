package com.github.clasicrando.workflows.model

import org.snappy.ksp.symbols.RowParser

@RowParser
data class Workflow(
    val id: WorkflowId,
    val name: String,
    val workflowDefinitionName: String,
    val pipelineState: String,
)
