package com.github.clasicrando.datasources.model

import org.snappy.ksp.symbols.Rename
import org.snappy.ksp.symbols.RowParser
import java.time.OffsetDateTime

@RowParser
data class DataSource(
    val dsId: DsId,
    val code: String,
    @Rename("prov")
    val province: String?,
    val country: String?,
    val provLevel: Boolean,
    val filesLocation: String,
    val comments: String?,
    val searchRadius: Double,
    val reportingType: String,
    val recordWarehouseType: String,
    val assignedUser: String,
    val createdBy: String,
    val created: OffsetDateTime,
    val updatedBy: String?,
    val lastUpdated: OffsetDateTime?,
    val collectionWorkflow: String,
    val loadWorkflow: String,
    val checkWorkflow: String,
    val qaWorkflow: String,
)
