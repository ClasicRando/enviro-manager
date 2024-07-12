package com.github.clasicrando.requests

import com.github.clasicrando.datasources.model.RecordWarehouseTypeId
import com.github.clasicrando.workflows.model.WorkflowId
import kotlinx.serialization.Serializable

@Serializable
data class CreateDateSourceRequest(
    val modalId: String,
    val code: String,
    val prov: String,
    val country: String,
    val description: String,
    val filesLocation: String,
    @Serializable(with = BooleanSwitchSerializer::class)
    val provLevel: Boolean = false,
    val comments: String,
    val assignedUser: String,
    @Serializable(with = DecimalInputSerializer::class)
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
            code.isBlank() -> "Code cannot be blank"
            provLevel && prov.isBlank() -> "Prov cannot be blank when the data source is prov level"
            country.isBlank() -> "Country cannot be blank"
            description.isBlank() -> "Description cannot be blank"
            filesLocation.isBlank() -> "Files location cannot be blank"
            assignedUser.isBlank() -> "Assigned user cannot be blank"
            searchRadius == 0.0 -> "Search radius cannot be zero"
            reportingType.isBlank() -> "Reporting type cannot be blank"
            else -> null
        }
}
