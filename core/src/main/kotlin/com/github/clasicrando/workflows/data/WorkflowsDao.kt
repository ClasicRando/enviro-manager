package com.github.clasicrando.workflows.data

import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowId

interface WorkflowsDao {
    suspend fun getById(id: WorkflowId): Workflow?

    suspend fun getAll(): List<Workflow>

    suspend fun createWorkflow(
        name: String,
        workflowDefinitionName: String,
        pipelineState: String,
    )

    suspend fun updateWorkflow(
        workflowId: WorkflowId,
        name: String,
        workflowDefinitionName: String,
        pipelineState: String,
    )
}
