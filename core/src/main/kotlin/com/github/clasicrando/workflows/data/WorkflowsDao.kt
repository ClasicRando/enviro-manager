package com.github.clasicrando.workflows.data

import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowId
import com.github.clasicrando.workflows.model.WorkflowTaskComposite

interface WorkflowsDao {
    suspend fun getById(id: WorkflowId): Workflow?

    suspend fun getAll(): List<Workflow>

    suspend fun createWorkflow(
        name: String,
        workflowTasks: List<WorkflowTaskComposite>,
    )

    suspend fun updateWorkflow(
        workflowId: WorkflowId,
        name: String,
        workflowTasks: List<WorkflowTaskComposite>,
    )
}
