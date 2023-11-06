package com.github.clasicrando.datasources.model

import com.github.clasicrando.jasync.symbol.Rename
import com.github.clasicrando.jasync.symbol.ResultRow
import java.time.OffsetDateTime

@ResultRow
data class DataSource(
    @Rename("ds_id")
    val dsId: DsId,
    val code: String,
    @Rename(name = "prov")
    val province: String?,
    val country: String?,
    @Rename("prov_level")
    val provLevel: Boolean,
    val description: String,
    @Rename("files_location")
    val filesLocation: String,
    val comments: String?,
    @Rename("search_radius")
    val searchRadius: Double,
    @Rename("reporting_type")
    val reportingType: String,
    @Rename("record_warehouse_type")
    val recordWarehouseType: String,
    @Rename("assigned_user")
    val assignedUser: String,
    @Rename("created_by")
    val createdBy: String,
    val created: OffsetDateTime,
    @Rename("updated_by")
    val updatedBy: String?,
    @Rename("last_updated")
    val lastUpdated: OffsetDateTime?,
    @Rename("collection_workflow")
    val collectionWorkflow: String,
    @Rename("load_workflow")
    val loadWorkflow: String,
    @Rename("check_workflow")
    val checkWorkflow: String,
    @Rename("qa_workflow")
    val qaWorkflow: String,
)
