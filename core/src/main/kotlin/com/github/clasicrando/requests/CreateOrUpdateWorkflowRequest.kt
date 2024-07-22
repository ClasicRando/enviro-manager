package com.github.clasicrando.requests

import com.github.clasicrando.workflows.model.TaskId
import com.github.clasicrando.workflows.model.WorkflowId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CreateOrUpdateWorkflowRequest(
    val modalId: String,
    val workflowId: WorkflowId? = null,
    val name: String,
    val taskOrders: UnknownNumberOfItems<Int>,
    val taskIds: UnknownNumberOfItems<TaskId>,
    val defaultInputParameters: UnknownNumberOfItems<JsonElement>,
) : ApiRequest {
    override fun validate(): String? =
        when {
            name.isBlank() -> "Name cannot be blank"
            taskOrders.isEmpty() -> "Must have at least 1 task"
            taskOrders.size != taskIds.size || taskOrders.size != defaultInputParameters.size -> {
                "Mismatch in number of task orders and IDs"
            }

            else -> null
        }
}
