package com.github.clasicrando.workflows.data

import com.github.clasicrando.workflows.model.WorkflowId
import com.github.clasicrando.workflows.model.WorkflowTask

interface WorkflowTasksDao {
    suspend fun getWorkflowTasks(workflowId: WorkflowId): List<WorkflowTask>
}
