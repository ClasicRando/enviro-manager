package com.github.clasicrando.datasources.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAs
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class DataSource(
    val dsId: DsId,
    val code: String,
    val province: String?,
    val country: String,
    val provLevel: Boolean,
    val description: String,
    val filesLocation: String,
    val comments: String?,
    val searchRadius: Double,
    val reportingType: String,
    val recordWarehouseType: String,
    val assignedUser: String,
    val createdBy: String,
    val created: Instant,
    val updatedBy: String?,
    val lastUpdated: Instant?,
    val collectionWorkflow: String,
    val loadWorkflow: String,
    val checkWorkflow: String,
    val qaWorkflow: String,
) {
    companion object : RowParser<DataSource> {
        override fun fromRow(row: DataRow): DataSource =
            DataSource(
                dsId = row.getAsNonNull<Long>("ds_id").toDsId(),
                code = row.getAsNonNull("code"),
                province = row.getAs("prov"),
                country = row.getAsNonNull("country"),
                provLevel = row.getAsNonNull("prov_level"),
                description = row.getAsNonNull("description"),
                filesLocation = row.getAsNonNull("files_location"),
                comments = row.getAs("comments"),
                searchRadius = row.getAsNonNull("search_radius"),
                reportingType = row.getAsNonNull("reporting_type"),
                recordWarehouseType = row.getAsNonNull("record_warehouse_type"),
                assignedUser = row.getAsNonNull("assigned_user"),
                createdBy = row.getAsNonNull("created_by"),
                created = row.getAsNonNull("created"),
                updatedBy = row.getAs("updated_by"),
                lastUpdated = row.getAs("last_updated"),
                collectionWorkflow = row.getAsNonNull("collection_workflow"),
                loadWorkflow = row.getAsNonNull("load_workflow"),
                checkWorkflow = row.getAsNonNull("check_workflow"),
                qaWorkflow = row.getAsNonNull("qa_workflow"),
            )
    }
}
