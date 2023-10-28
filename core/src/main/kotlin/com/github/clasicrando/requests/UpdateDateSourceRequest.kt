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
)
