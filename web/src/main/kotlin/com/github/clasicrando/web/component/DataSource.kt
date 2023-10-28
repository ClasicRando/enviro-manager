package com.github.clasicrando.web.component

import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DataSourceWithContacts
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.row
import io.ktor.http.HttpMethod
import kotlinx.html.FlowContent
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

fun FlowContent.dataSourceDisplay(
    data: DataSourceWithContacts,
    edit: Boolean = false,
) {
    fieldSet {
        dataGroup(title = "Details") {
            row {
                dataDisplayField(
                    fieldId = "dsId",
                    label = "ID",
                    columnWidth = 1,
                    data = data.dataSource.dsId,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "code",
                    label = "Code",
                    columnWidth = 1,
                    data = data.dataSource.code,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "prov",
                    label = "Province",
                    columnWidth = 1,
                    data = data.dataSource.province,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "country",
                    label = "Country",
                    columnWidth = 1,
                    data = data.dataSource.country,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "provLevel",
                    label = "Prov Level?",
                    columnWidth = 1,
                    data = data.dataSource.provLevel,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "searchRadius",
                    label = "Search Radius",
                    columnWidth = 1,
                    data = data.dataSource.searchRadius,
                    edit = edit,
                )
            }
            row {
                dataDisplayField(
                    fieldId = "filesLocation",
                    label = "Files Location",
                    columnWidth = 3,
                    data = data.dataSource.filesLocation,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "reportingType",
                    label = "Reporting Type",
                    columnWidth = 1,
                    data = data.dataSource.reportingType,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "recordWarehouseType",
                    label = "Record Warehouse Type",
                    columnWidth = 1,
                    data = data.dataSource.recordWarehouseType,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "assignedUser",
                    label = "Assigned User",
                    columnWidth = 3,
                    data = data.dataSource.assignedUser,
                    edit = edit,
                )
            }
            row {
                dataDisplayField(
                    fieldId = "createdBy",
                    label = "Created By",
                    columnWidth = 5,
                    data = data.dataSource.createdBy,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "created",
                    label = "Created",
                    columnWidth = 5,
                    data = data.dataSource.created,
                    edit = edit,
                )
            }
            row {
                dataDisplayField(
                    fieldId = "updatedBy",
                    label = "Updated By",
                    columnWidth = 5,
                    data = data.dataSource.updatedBy,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "lastUpdated",
                    label = "Last Updated",
                    columnWidth = 5,
                    data = data.dataSource.lastUpdated,
                    edit = edit,
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
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "loadWorkflow",
                    label = "Load",
                    columnWidth = 2,
                    data = data.dataSource.loadWorkflow,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "checkWorkflow",
                    label = "Check",
                    columnWidth = 2,
                    data = data.dataSource.checkWorkflow,
                    edit = edit,
                )
                dataDisplayField(
                    fieldId = "qaWorkflow",
                    label = "QA",
                    columnWidth = 2,
                    data = data.dataSource.qaWorkflow,
                    edit = edit,
                )
            }
        }
    }
    if (!edit) {
        dataDisplayTable(
            caption = "Contacts",
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
}
