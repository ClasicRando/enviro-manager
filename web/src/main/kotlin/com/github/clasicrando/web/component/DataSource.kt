package com.github.clasicrando.web.component

import com.github.clasicrando.datasources.model.DataSource
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.Row
import com.github.clasicrando.web.htmx.HxSwap
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.hxInclude
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
fun <T, C : TagConsumer<T>> C.DataSourceTable(user: User) {
    val extraButtons =
        if (user.hasRole(Role.CreateDataSource)) {
            listOf(
                ExtraButton(
                    title = "Create New Data Source",
                    apiUrl = apiV1Url("/data-sources/create"),
                    icon = "fa-plus",
                    target = ADD_MODAL_TARGET,
                    swap = HxSwap(swapType = SwapType.BeforeEnd),
                    httpMethod = HttpMethod.Get,
                ),
            )
        } else {
            emptyList()
        }
    DataTableRefresh(
        id = DATA_SOURCES_TABLE,
        title = "Data Sources",
        dataSource = apiV1Url("/data-sources"),
        extraButtons = extraButtons,
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
        DataCell(dataSource.dsId)
        DataCell(dataSource.code)
        DataCell(dataSource.province)
        DataCell(dataSource.country)
        td {
            i(classes = "fa-solid ${if (dataSource.provLevel) "fa-check" else "fa-x"}")
        }
        DataCell(dataSource.reportingType)
        DataCell(dataSource.assignedUser)
        DataCell(dataSource.createdBy)
        DataCell(dataSource.created)
        DataCell(dataSource.updatedBy)
        DataCell(dataSource.lastUpdated)
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
fun <T, C : TagConsumer<T>> C.DataSourceView(
    dsId: DsId,
    user: User,
) {
    DataDisplay(
        id = "dataSourceView",
        title = "Data Source Details",
        dataUrl = apiV1Url("/data-sources/$dsId"),
        editUrl =
            if (user.hasRole(Role.EditDataSource)) {
                apiV1Url("/data-sources/$dsId/edit")
            } else {
                null
            },
    )
}

@Component
fun <T, C : TagConsumer<T>> C.CreateDataSourceModal(collectionUsers: List<User>) {
    CreateModal(
        id = "createDataSource",
        title = "Create Data Source",
        postUrl = apiV1Url("/data-sources"),
        target = NO_DISPLAY_ELEMENT_TARGET,
        modalSize = ModalSize.ExtraLarge,
    ) {
        fieldSet {
            DataGroup(title = "Details") {
                Row {
                    DataEditField(
                        fieldId = "code",
                        label = "Code",
                        columnWidth = 1,
                    )
                    DataSelectionField(
                        fieldId = "prov",
                        label = "Province",
                        columnWidth = 2,
                        dataUrl = apiV1Url("/regions/provinces"),
                        trigger = "refresh-provinces from:body, change from:#country",
                    ) {
                        hxInclude = "#country"
                    }
                    DataSelectionField(
                        fieldId = "country",
                        label = "Country",
                        columnWidth = 2,
                        dataUrl = apiV1Url("/regions/countries"),
                        trigger = "load",
                    )
                    DataEditField(
                        fieldId = "searchRadius",
                        label = "Search Radius",
                        columnWidth = 1,
                        data = 0.25,
                        inputType = InputType.tel,
                        labelColumnWidth = 2,
                    )
                }
                Row {
                    DataEditField(
                        fieldId = "reportingType",
                        label = "Reporting Type",
                        columnWidth = 1,
                        labelColumnWidth = 2,
                    )
                    DataSelectionField(
                        fieldId = "recordWarehouseTypeId",
                        label = "Record Warehouse Type",
                        columnWidth = 2,
                        dataUrl = apiV1Url("/data-sources/record-warehouse-types"),
                        trigger = "load",
                        labelColumnWidth = 3,
                    )
                    DataSelectionField(
                        fieldId = "assignedUser",
                        label = "Assigned User",
                        columnWidth = 2,
                        selectionItems = collectionUsers.map { it.username to it.fullName },
                        labelColumnWidth = 2,
                    )
                }
                Row(classes = "m-1") {
                    DataEditField(
                        fieldId = "filesLocation",
                        label = "Files Location",
                        columnWidth = 10,
                        labelColumnWidth = 2,
                    )
                }
                Row {
                    DataEditArea(
                        fieldId = "description",
                        label = "Description",
                        columnWidth = 5,
                    )
                    DataEditArea(
                        fieldId = "comments",
                        label = "Comments",
                        columnWidth = 5,
                    )
                }
            }
            DataGroup(title = "Workflows", topMargin = 4u) {
                Row {
                    DataSelectionField(
                        fieldId = "collectionWorkflowId",
                        label = "Collection",
                        columnWidth = 2,
                        dataUrl = apiV1Url("/workflows/collection"),
                        trigger = "load",
                    )
                    DataSelectionField(
                        fieldId = "loadWorkflowId",
                        label = "Load",
                        columnWidth = 2,
                        dataUrl = apiV1Url("/workflows/load"),
                        trigger = "load",
                    )
                    DataSelectionField(
                        fieldId = "checkWorkflowId",
                        label = "Check",
                        columnWidth = 2,
                        dataUrl = apiV1Url("/workflows/check"),
                        trigger = "load",
                    )
                    DataSelectionField(
                        fieldId = "qaWorkflowId",
                        label = "QA",
                        columnWidth = 2,
                        dataUrl = apiV1Url("/workflows/qa"),
                        trigger = "load",
                    )
                }
            }
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.DataSourceEditForm(
    dataSource: DataSource,
    collectionUsers: List<User>,
) {
    DataEdit(
        title = "Edit Data Source Details",
        patchUrl = apiV1Url("/data-sources/${dataSource.dsId}"),
        cancelUrl = "/data-sources/${dataSource.dsId}",
    ) {
        DataSourceEdit(
            dataSource = dataSource,
            collectionUsers = collectionUsers,
        )
    }
}

@Component
fun FlowContent.DataSourceEdit(
    dataSource: DataSource,
    collectionUsers: List<User>,
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
                val current = dataSource.recordWarehouseType
                val warehouseUrl =
                    apiV1Url(
                        "/data-sources/record-warehouse-types?current=$current",
                    )
                DataSelectionField(
                    fieldId = "recordWarehouseTypeId",
                    label = "Record Warehouse Type",
                    columnWidth = 1,
                    dataUrl = warehouseUrl,
                    trigger = "load",
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
                    dataUrl =
                        apiV1Url(
                            "/workflows/collection?current=${dataSource.collectionWorkflow}",
                        ),
                    trigger = "load",
                )
                DataSelectionField(
                    fieldId = "loadWorkflowId",
                    label = "Load",
                    columnWidth = 2,
                    dataUrl =
                        apiV1Url(
                            "/workflows/load?current=${dataSource.collectionWorkflow}",
                        ),
                    trigger = "load",
                )
                DataSelectionField(
                    fieldId = "checkWorkflowId",
                    label = "Check",
                    columnWidth = 2,
                    dataUrl =
                        apiV1Url(
                            "/workflows/check?current=${dataSource.collectionWorkflow}",
                        ),
                    trigger = "load",
                )
                DataSelectionField(
                    fieldId = "qaWorkflowId",
                    label = "QA",
                    columnWidth = 2,
                    dataUrl =
                        apiV1Url(
                            "/workflows/qa?current=${dataSource.collectionWorkflow}",
                        ),
                    trigger = "load",
                )
            }
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.DataSourceDisplay(
    dataSource: DataSource,
    user: User,
) {
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
    val extraButtons =
        if (user.hasRole(Role.EditDataSource)) {
            listOf(
                ExtraButton(
                    title = "New Contact",
                    apiUrl = apiV1Url("/data-sources/$dsId/contacts/create"),
                    icon = "fa-plus",
                    httpMethod = HttpMethod.Get,
                    target = ADD_MODAL_TARGET,
                ),
            )
        } else {
            emptyList()
        }
    DataTableRefresh(
        id = "dataSourceContacts",
        title = "Contacts",
        dataSource = apiV1Url("/data-sources/$dsId/contacts"),
        extraButtons = extraButtons,
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
