package com.github.clasicrando.web.component

import com.github.clasicrando.models.DataSource
import com.github.clasicrando.models.DataSourceWithContacts
import com.github.clasicrando.web.element.row
import com.github.clasicrando.web.htmx.HtmxContentCollector
import io.ktor.http.HttpMethod
import kotlinx.html.fieldSet
import kotlinx.html.i
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

fun HtmxContentCollector.dataSourceRow(dataSource: DataSource) {
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
                url = "/data-source/${dataSource.dsId}",
                icon = "fa-right-to-bracket",
                httpMethod = HttpMethod.Get,
            )
        }
    }
}

fun HtmxContentCollector.dataSourceDisplay(data: DataSourceWithContacts) {
    fieldSet {
        dataGroup(title = "Details") {
            row {
                dataField(
                    fieldId = "dsId",
                    label = "ID",
                    columnWidth = 1,
                    data = data.dataSource.dsId,
                )
                dataField(
                    fieldId = "code",
                    label = "Code",
                    columnWidth = 1,
                    data = data.dataSource.code,
                )
                dataField(
                    fieldId = "prov",
                    label = "Province",
                    columnWidth = 1,
                    data = data.dataSource.province,
                )
                dataField(
                    fieldId = "country",
                    label = "Country",
                    columnWidth = 1,
                    data = data.dataSource.country,
                )
                dataField(
                    fieldId = "provLevel",
                    label = "Prov Level?",
                    columnWidth = 1,
                    data = data.dataSource.provLevel,
                )
                dataField(
                    fieldId = "searchRadius",
                    label = "Search Radius",
                    columnWidth = 1,
                    data = data.dataSource.searchRadius,
                )
            }
            row {
                dataField(
                    fieldId = "filesLocation",
                    label = "Files Location",
                    columnWidth = 3,
                    data = data.dataSource.filesLocation,
                )
                dataField(
                    fieldId = "reportingType",
                    label = "Reporting Type",
                    columnWidth = 1,
                    data = data.dataSource.reportingType,
                )
                dataField(
                    fieldId = "recordWarehouseType",
                    label = "Record Warehouse Type",
                    columnWidth = 1,
                    data = data.dataSource.recordWarehouseType,
                )
                dataField(
                    fieldId = "assignedUser",
                    label = "Assigned User",
                    columnWidth = 3,
                    data = data.dataSource.assignedUser,
                )
            }
            row {
                dataField(
                    fieldId = "createdBy",
                    label = "Created By",
                    columnWidth = 5,
                    data = data.dataSource.createdBy,
                )
                dataField(
                    fieldId = "created",
                    label = "Created",
                    columnWidth = 5,
                    data = data.dataSource.created,
                )
            }
            row {
                dataField(
                    fieldId = "updatedBy",
                    label = "Updated By",
                    columnWidth = 5,
                    data = data.dataSource.updatedBy,
                )
                dataField(
                    fieldId = "lastUpdated",
                    label = "Last Updated",
                    columnWidth = 5,
                    data = data.dataSource.lastUpdated,
                )
            }
        }
        dataGroup(title = "Workflows", topMargin = 4u) {
            row {
                dataField(
                    fieldId = "collectionWorkflow",
                    label = "Collection",
                    columnWidth = 2,
                    data = data.dataSource.collectionWorkflow,
                )
                dataField(
                    fieldId = "loadWorkflow",
                    label = "Load",
                    columnWidth = 2,
                    data = data.dataSource.loadWorkflow,
                )
                dataField(
                    fieldId = "checkWorkflow",
                    label = "Check",
                    columnWidth = 2,
                    data = data.dataSource.checkWorkflow,
                )
                dataField(
                    fieldId = "qaWorkflow",
                    label = "QA",
                    columnWidth = 2,
                    data = data.dataSource.qaWorkflow,
                )
            }
        }
    }
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
