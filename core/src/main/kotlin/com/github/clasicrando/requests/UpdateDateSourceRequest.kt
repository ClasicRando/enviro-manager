package com.github.clasicrando.requests

import com.github.clasicrando.datasources.model.RecordWarehouseTypeId
import com.github.clasicrando.workflows.model.WorkflowId
import kotlinx.serialization.Serializable

@Serializable
data class UpdateDateSourceRequest(
    val description: String,
    val filesLocation: String,
    val comments: String,
    val assignedUser: String,
    val searchRadius: Double,
    val recordWarehouseTypeId: RecordWarehouseTypeId,
    val reportingType: String,
    val collectionWorkflowId: WorkflowId,
    val loadWorkflowId: WorkflowId,
    val checkWorkflowId: WorkflowId,
    val qaWorkflowId: WorkflowId,
) : ApiRequest {
    override fun validate(): String? =
        when {
            description.isBlank() -> "Description cannot be blank"
            filesLocation.isBlank() -> "Files location cannot be blank"
            assignedUser.isBlank() -> "Assigned user cannot be blank"
            searchRadius != 0.0 -> "Search radius cannot be zero"
            reportingType.isBlank() -> "Reporting type cannot be blank"
            else -> null
        }
}
