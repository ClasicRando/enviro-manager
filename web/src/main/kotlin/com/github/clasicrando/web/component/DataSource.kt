package com.github.clasicrando.web.component

import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DataSourceWithContacts
import com.github.clasicrando.datasources.model.RecordWarehouseType
import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.api.DATA_SOURCE_API_BASE_URL
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.row
import com.github.clasicrando.workflows.model.Workflow
import io.ktor.http.HttpMethod
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.fieldSet
import kotlinx.html.i
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

fun TBODY.dataSourceRow(dataSource: DataSource) {
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
            rowAction(
                title = "View Data Source",
                url = apiV1Url("/data-sources/${dataSource.dsId}"),
                icon = "fa-right-to-bracket",
                httpMethod = HttpMethod.Get,
            )
        }
    }
}

fun FlowContent.dataSourceEdit(
    dataSource: DataSource,
    recordWarehouseTypes: List<RecordWarehouseType>,
    collectionUsers: List<User>,
    workflows: List<Workflow>,
) {
    fieldSet {
        dataGroup(title = "Details") {
            row {
                dataDisplayField(
                    fieldId = "dsId",
                    label = "ID",
                    columnWidth = 1,
                    data = dataSource.dsId,
                )
                dataDisplayField(
                    fieldId = "code",
                    label = "Code",
                    columnWidth = 1,
                    data = dataSource.code,
                )
                dataDisplayField(
                    fieldId = "prov",
                    label = "Province",
                    columnWidth = 1,
                    data = dataSource.province,
                )
                dataDisplayField(
                    fieldId = "country",
                    label = "Country",
                    columnWidth = 1,
                    data = dataSource.country,
                )
                dataDisplayField(
                    fieldId = "provLevel",
                    label = "Prov Level?",
                    columnWidth = 1,
                    data = dataSource.provLevel,
                )
                dataEditField(
                    fieldId = "searchRadius",
                    label = "Search Radius",
                    columnWidth = 1,
                    data = dataSource.searchRadius,
                    inputType = InputType.tel,
                )
            }
            row {
                dataEditField(
                    fieldId = "filesLocation",
                    label = "Files Location",
                    columnWidth = 3,
                    data = dataSource.filesLocation,
                )
                dataEditField(
                    fieldId = "reportingType",
                    label = "Reporting Type",
                    columnWidth = 1,
                    data = dataSource.reportingType,
                )
                dataSelectionField(
                    fieldId = "recordWarehouseTypeId",
                    label = "Record Warehouse Type",
                    columnWidth = 1,
                    selectionItems = recordWarehouseTypes.map { it.id.toString() to it.name },
                    initDisplay = dataSource.recordWarehouseType,
                )
                dataSelectionField(
                    fieldId = "assignedUser",
                    label = "Assigned User",
                    columnWidth = 3,
                    selectionItems = collectionUsers.map { it.username to it.fullName },
                    initDisplay = dataSource.assignedUser,
                )
            }
            row {
                dataEditArea(
                    fieldId = "description",
                    label = "Description",
                    columnWidth = 5,
                    data = dataSource.description,
                )
                dataEditArea(
                    fieldId = "comments",
                    label = "Comments",
                    columnWidth = 5,
                    data = dataSource.comments,
                )
            }
        }
        dataGroup(title = "Workflows", topMargin = 4u) {
            row {
                dataSelectionField(
                    fieldId = "collectionWorkflowId",
                    label = "Collection",
                    columnWidth = 2,
                    selectionItems =
                        workflows.asSequence()
                            .filter { it.pipelineState == "Data Collection" }
                            .map { it.id.toString() to it.name }
                            .toList(),
                    initDisplay = dataSource.collectionWorkflow,
                )
                dataSelectionField(
                    fieldId = "loadWorkflowId",
                    label = "Load",
                    columnWidth = 2,
                    selectionItems =
                        workflows.asSequence()
                            .filter { it.pipelineState == "Data Loading" }
                            .map { it.id.toString() to it.name }
                            .toList(),
                    initDisplay = dataSource.loadWorkflow,
                )
                dataSelectionField(
                    fieldId = "checkWorkflowId",
                    label = "Check",
                    columnWidth = 2,
                    selectionItems =
                        workflows.asSequence()
                            .filter { it.pipelineState == "Load Checking" }
                            .map { it.id.toString() to it.name }
                            .toList(),
                    initDisplay = dataSource.checkWorkflow,
                )
                dataSelectionField(
                    fieldId = "qaWorkflowId",
                    label = "QA",
                    columnWidth = 2,
                    selectionItems =
                        workflows.asSequence()
                            .filter { it.pipelineState == "Load QA" }
                            .map { it.id.toString() to it.name }
                            .toList(),
                    initDisplay = dataSource.qaWorkflow,
                )
            }
        }
    }
}

fun FlowContent.dataSourceDisplay(data: DataSourceWithContacts) {
    fieldSet {
        dataGroup(title = "Details") {
            row {
                dataDisplayField(
                    fieldId = "dsId",
                    label = "ID",
                    columnWidth = 1,
                    data = data.dataSource.dsId,
                )
                dataDisplayField(
                    fieldId = "code",
                    label = "Code",
                    columnWidth = 1,
                    data = data.dataSource.code,
                )
                dataDisplayField(
                    fieldId = "prov",
                    label = "Province",
                    columnWidth = 1,
                    data = data.dataSource.province,
                )
                dataDisplayField(
                    fieldId = "country",
                    label = "Country",
                    columnWidth = 1,
                    data = data.dataSource.country,
                )
                dataDisplayField(
                    fieldId = "provLevel",
                    label = "Prov Level?",
                    columnWidth = 1,
                    data = data.dataSource.provLevel,
                )
                dataDisplayField(
                    fieldId = "searchRadius",
                    label = "Search Radius",
                    columnWidth = 1,
                    data = data.dataSource.searchRadius,
                )
            }
            row {
                dataDisplayField(
                    fieldId = "filesLocation",
                    label = "Files Location",
                    columnWidth = 3,
                    data = data.dataSource.filesLocation,
                )
                dataDisplayField(
                    fieldId = "reportingType",
                    label = "Reporting Type",
                    columnWidth = 1,
                    data = data.dataSource.reportingType,
                )
                dataDisplayField(
                    fieldId = "recordWarehouseType",
                    label = "Record Warehouse Type",
                    columnWidth = 1,
                    data = data.dataSource.recordWarehouseType,
                )
                dataDisplayField(
                    fieldId = "assignedUser",
                    label = "Assigned User",
                    columnWidth = 3,
                    data = data.dataSource.assignedUser,
                )
            }
            row {
                dataDisplayArea(
                    fieldId = "description",
                    label = "Description",
                    columnWidth = 5,
                    data = data.dataSource.description,
                )
                dataDisplayArea(
                    fieldId = "comments",
                    label = "Comments",
                    columnWidth = 5,
                    data = data.dataSource.comments,
                )
            }
            row {
                dataDisplayField(
                    fieldId = "createdBy",
                    label = "Created By",
                    columnWidth = 5,
                    data = data.dataSource.createdBy,
                )
                dataDisplayField(
                    fieldId = "created",
                    label = "Created",
                    columnWidth = 5,
                    data = data.dataSource.created,
                )
            }
            row {
                dataDisplayField(
                    fieldId = "updatedBy",
                    label = "Updated By",
                    columnWidth = 5,
                    data = data.dataSource.updatedBy,
                )
                dataDisplayField(
                    fieldId = "lastUpdated",
                    label = "Last Updated",
                    columnWidth = 5,
                    data = data.dataSource.lastUpdated,
                )
            }
        }
        dataGroup(title = "Workflows", topMargin = 4u) {
            row {
                dataDisplayField(
                    fieldId = "collectionWorkflow",
                    label = "Collection",
                    columnWidth = 2,
                    data = data.dataSource.collectionWorkflow,
                )
                dataDisplayField(
                    fieldId = "loadWorkflow",
                    label = "Load",
                    columnWidth = 2,
                    data = data.dataSource.loadWorkflow,
                )
                dataDisplayField(
                    fieldId = "checkWorkflow",
                    label = "Check",
                    columnWidth = 2,
                    data = data.dataSource.checkWorkflow,
                )
                dataDisplayField(
                    fieldId = "qaWorkflow",
                    label = "QA",
                    columnWidth = 2,
                    data = data.dataSource.qaWorkflow,
                )
            }
        }
    }
    val addContact =
        ExtraButton(
            title = "New Contact",
            apiUrl = apiV1Url("$DATA_SOURCE_API_BASE_URL/${data.dataSource.dsId}/contact/create"),
            icon = "fa-plus",
            httpMethod = HttpMethod.Get,
        )
    dataTable(
        title = "Contacts",
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
            }
        },
        items = data.contacts ?: emptyList(),
        rowBuilder = { contact ->
            tr {
                dataCell(contact.contactId)
                dataCell(contact.name)
                dataCell(contact.email)
                dataCell(contact.website)
                dataCell(contact.type)
                dataCell(contact.notes)
            }
        },
    )
}
