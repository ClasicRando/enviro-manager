package com.github.clasicrando.web.component

import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.datasources.model.RecordWarehouseType
import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.Row
import com.github.clasicrando.workflows.model.Workflow
import io.ktor.http.HttpMethod
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.fieldSet
import kotlinx.html.i
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

const val DATA_SOURCES_TABLE = "dataSourcesTable"

@Component
fun <T, C : TagConsumer<T>> C.DataSourceTableRefresh() {
    DataTableRefresh(
        id = DATA_SOURCES_TABLE,
        title = "Data Sources",
        dataSource = apiV1Url("/data-sources"),
        header = {
            tr {
                th { +"Id" }
                th { +"Code" }
                th { +"Province" }
                th { +"Country" }
                th { +"Prov Level" }
                th { +"Reporting Type" }
                th { +"Assigned User" }
                th { +"Created By" }
                th { +"Created" }
                th { +"Updated By" }
                th { +"Last Updated" }
                th { +"Actions" }
            }
        },
    )
}

@Component
fun TBODY.DataSource(dataSource: DataSource) {
    tr {
        dataCell(dataSource.dsId)
        dataCell(dataSource.code)
        dataCell(dataSource.province)
        dataCell(dataSource.country)
        td {
            i(classes = "fa-solid ${if (dataSource.provLevel) "fa-check" else "fa-x"}")
        }
        dataCell(dataSource.reportingType)
        dataCell(dataSource.assignedUser)
        dataCell(dataSource.createdBy)
        dataCell(dataSource.created)
        dataCell(dataSource.updatedBy)
        dataCell(dataSource.lastUpdated)
        td {
            RowAction(
                title = "View Data Source",
                url = "/data-sources/${dataSource.dsId}",
                icon = "fa-right-to-bracket",
                httpMethod = HttpMethod.Get,
            )
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.DataSourceView(dsId: DsId) {
    DataDisplay(
        id = "dataSourceView",
        title = "Data Source Details",
        dataUrl = apiV1Url("/data-sources/$dsId"),
        editUrl = apiV1Url("/data-sources/$dsId/edit"),
    )
}

@Component
fun <T, C : TagConsumer<T>> C.DataSourceEditForm(
    dataSource: DataSource,
    recordWarehouseTypes: List<RecordWarehouseType>,
    collectionUsers: List<User>,
    workflows: List<Workflow>,
) {
    DataEdit(
        title = "Edit Data Source Details",
        patchUrl = apiV1Url("/data-sources/${dataSource.dsId}"),
        cancelUrl = "/data-sources/${dataSource.dsId}",
    ) {
        DataSourceEdit(
            dataSource = dataSource,
            recordWarehouseTypes = recordWarehouseTypes,
            collectionUsers = collectionUsers,
            workflows = workflows,
        )
    }
}

@Component
fun FlowContent.DataSourceEdit(
    dataSource: DataSource,
    recordWarehouseTypes: List<RecordWarehouseType>,
    collectionUsers: List<User>,
    workflows: List<Workflow>,
) {
    fieldSet {
        DataGroup(title = "Details") {
            Row {
                DataDisplayField(
                    fieldId = "dsId",
                    label = "ID",
                    columnWidth = 1,
                    data = dataSource.dsId,
                )
                DataDisplayField(
                    fieldId = "code",
                    label = "Code",
                    columnWidth = 1,
                    data = dataSource.code,
                )
                DataDisplayField(
                    fieldId = "prov",
                    label = "Province",
                    columnWidth = 1,
                    data = dataSource.province,
                )
                DataDisplayField(
                    fieldId = "country",
                    label = "Country",
                    columnWidth = 1,
                    data = dataSource.country,
                )
                DataDisplayField(
                    fieldId = "provLevel",
                    label = "Prov Level?",
                    columnWidth = 1,
                    data = dataSource.provLevel,
                )
                DataEditField(
                    fieldId = "searchRadius",
                    label = "Search Radius",
                    columnWidth = 1,
                    data = dataSource.searchRadius,
                    inputType = InputType.tel,
                )
            }
            Row {
                DataEditField(
                    fieldId = "filesLocation",
                    label = "Files Location",
                    columnWidth = 3,
                    data = dataSource.filesLocation,
                )
                DataEditField(
                    fieldId = "reportingType",
                    label = "Reporting Type",
                    columnWidth = 1,
                    data = dataSource.reportingType,
                )
                DataSelectionField(
                    fieldId = "recordWarehouseTypeId",
                    label = "Record Warehouse Type",
                    columnWidth = 1,
                    selectionItems = recordWarehouseTypes.map { it.id.toString() to it.name },
                    initDisplay = dataSource.recordWarehouseType,
                )
                DataSelectionField(
                    fieldId = "assignedUser",
                    label = "Assigned User",
                    columnWidth = 3,
                    selectionItems = collectionUsers.map { it.username to it.fullName },
                    initDisplay = dataSource.assignedUser,
                )
            }
            Row {
                DataEditArea(
                    fieldId = "description",
                    label = "Description",
                    columnWidth = 5,
                    data = dataSource.description,
                )
                DataEditArea(
                    fieldId = "comments",
                    label = "Comments",
                    columnWidth = 5,
                    data = dataSource.comments,
                )
            }
        }
        DataGroup(title = "Workflows", topMargin = 4u) {
            Row {
                DataSelectionField(
                    fieldId = "collectionWorkflowId",
                    label = "Collection",
                    columnWidth = 2,
                    selectionItems =
                        workflows
                            .asSequence()
                            .filter { it.pipelineState == "Data Collection" }
                            .map { it.id.toString() to it.name }
                            .toList(),
                    initDisplay = dataSource.collectionWorkflow,
                )
                DataSelectionField(
                    fieldId = "loadWorkflowId",
                    label = "Load",
                    columnWidth = 2,
                    selectionItems =
                        workflows
                            .asSequence()
                            .filter { it.pipelineState == "Data Loading" }
                            .map { it.id.toString() to it.name }
                            .toList(),
                    initDisplay = dataSource.loadWorkflow,
                )
                DataSelectionField(
                    fieldId = "checkWorkflowId",
                    label = "Check",
                    columnWidth = 2,
                    selectionItems =
                        workflows
                            .asSequence()
                            .filter { it.pipelineState == "Load Checking" }
                            .map { it.id.toString() to it.name }
                            .toList(),
                    initDisplay = dataSource.checkWorkflow,
                )
                DataSelectionField(
                    fieldId = "qaWorkflowId",
                    label = "QA",
                    columnWidth = 2,
                    selectionItems =
                        workflows
                            .asSequence()
                            .filter { it.pipelineState == "Load QA" }
                            .map { it.id.toString() to it.name }
                            .toList(),
                    initDisplay = dataSource.qaWorkflow,
                )
            }
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.DataSourceDisplay(dataSource: DataSource) {
    val dsId = dataSource.dsId
    fieldSet {
        DataGroup(title = "Details") {
            Row {
                DataDisplayField(
                    fieldId = "dsId",
                    label = "ID",
                    columnWidth = 1,
                    data = dsId,
                )
                DataDisplayField(
                    fieldId = "code",
                    label = "Code",
                    columnWidth = 1,
                    data = dataSource.code,
                )
                DataDisplayField(
                    fieldId = "prov",
                    label = "Province",
                    columnWidth = 1,
                    data = dataSource.province,
                )
                DataDisplayField(
                    fieldId = "country",
                    label = "Country",
                    columnWidth = 1,
                    data = dataSource.country,
                )
                DataDisplayField(
                    fieldId = "provLevel",
                    label = "Prov Level?",
                    columnWidth = 1,
                    data = dataSource.provLevel,
                )
                DataDisplayField(
                    fieldId = "searchRadius",
                    label = "Search Radius",
                    columnWidth = 1,
                    data = dataSource.searchRadius,
                )
            }
            Row {
                DataDisplayField(
                    fieldId = "filesLocation",
                    label = "Files Location",
                    columnWidth = 3,
                    data = dataSource.filesLocation,
                )
                DataDisplayField(
                    fieldId = "reportingType",
                    label = "Reporting Type",
                    columnWidth = 1,
                    data = dataSource.reportingType,
                )
                DataDisplayField(
                    fieldId = "recordWarehouseType",
                    label = "Record Warehouse Type",
                    columnWidth = 1,
                    data = dataSource.recordWarehouseType,
                )
                DataDisplayField(
                    fieldId = "assignedUser",
                    label = "Assigned User",
                    columnWidth = 3,
                    data = dataSource.assignedUser,
                )
            }
            Row {
                DataDisplayArea(
                    fieldId = "description",
                    label = "Description",
                    columnWidth = 5,
                    data = dataSource.description,
                )
                DataDisplayArea(
                    fieldId = "comments",
                    label = "Comments",
                    columnWidth = 5,
                    data = dataSource.comments,
                )
            }
            Row {
                DataDisplayField(
                    fieldId = "createdBy",
                    label = "Created By",
                    columnWidth = 5,
                    data = dataSource.createdBy,
                )
                DataDisplayField(
                    fieldId = "created",
                    label = "Created",
                    columnWidth = 5,
                    data = dataSource.created,
                )
            }
            Row {
                DataDisplayField(
                    fieldId = "updatedBy",
                    label = "Updated By",
                    columnWidth = 5,
                    data = dataSource.updatedBy,
                )
                DataDisplayField(
                    fieldId = "lastUpdated",
                    label = "Last Updated",
                    columnWidth = 5,
                    data = dataSource.lastUpdated,
                )
            }
        }
        DataGroup(title = "Workflows", topMargin = 4u) {
            Row {
                DataDisplayField(
                    fieldId = "collectionWorkflow",
                    label = "Collection",
                    columnWidth = 2,
                    data = dataSource.collectionWorkflow,
                )
                DataDisplayField(
                    fieldId = "loadWorkflow",
                    label = "Load",
                    columnWidth = 2,
                    data = dataSource.loadWorkflow,
                )
                DataDisplayField(
                    fieldId = "checkWorkflow",
                    label = "Check",
                    columnWidth = 2,
                    data = dataSource.checkWorkflow,
                )
                DataDisplayField(
                    fieldId = "qaWorkflow",
                    label = "QA",
                    columnWidth = 2,
                    data = dataSource.qaWorkflow,
                )
            }
        }
    }
    val addContact =
        ExtraButton(
            title = "New Contact",
            apiUrl = apiV1Url("/data-sources/$dsId/contacts/create"),
            icon = "fa-plus",
            httpMethod = HttpMethod.Get,
            target = ADD_MODAL_TARGET,
        )
    DataTableRefresh(
        id = "dataSourceContacts",
        title = "Contacts",
        dataSource = apiV1Url("/data-sources/$dsId/contacts"),
        extraButtons = listOf(addContact),
        extraContainerClasses = "mt-2",
        header = {
            tr {
                th { +"Contact ID" }
                th { +"Name" }
                th { +"Email" }
                th { +"Website" }
                th { +"Type" }
                th { +"Notes" }
                th { +"Actions" }
            }
        },
    )
}
