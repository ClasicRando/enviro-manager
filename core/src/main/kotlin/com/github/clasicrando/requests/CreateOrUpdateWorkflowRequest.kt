package com.github.clasicrando.requests

import com.github.clasicrando.workflows.model.WorkflowId
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrUpdateWorkflowRequest(
    val modalId: String,
    val workflowId: WorkflowId? = null,
    val name: String,
    val workflowDefinitionName: String,
    val pipelineState: String,
) : ApiRequest {
    override fun validate(): String? =
        when {
            name.isBlank() -> "Name cannot be blank"
            workflowDefinitionName.isBlank() -> "Workflow definition name cannot be blank"
            pipelineState.isBlank() -> "Pipeline state cannot be blank"
            else -> null
        }
}
