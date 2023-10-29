package com.github.clasicrando.web.api

import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.requests.UpdateDateSourceRequest
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.web.component.createDataSourceContactForm
import com.github.clasicrando.web.component.dataDisplay
import com.github.clasicrando.web.component.dataEdit
import com.github.clasicrando.web.component.dataSourceDisplay
import com.github.clasicrando.web.component.dataSourceEdit
import com.github.clasicrando.web.component.dataSourceRow
import com.github.clasicrando.web.component.dataTable
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.userSessionOrRedirect
import com.github.clasicrando.workflows.data.WorkflowsDao
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.th
import kotlinx.html.tr
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

const val DATA_SOURCE_API_BASE_URL = "/data-sources"

fun Route.dataSources() =
    route(DATA_SOURCE_API_BASE_URL) {
        getAllDataSources()
        getDataSource()
        editDataSource()
        route("/{dsId}/contact") {
            createContactForm()
            createContact()
        }
    }

private fun Route.getAllDataSources() =
    get {
        val dataSourcesDao: DataSourcesDao by closestDI().instance()
        val dataSources = dataSourcesDao.getAll()
        call.respondHtmx {
            addHtml {
                dataTable(
                    title = "Data Sources",
                    dataSource = call.request.uri,
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
                    items = dataSources,
                ) {
                    dataSourceRow(it)
                }
            }
        }
    }

private fun Route.getDataSource() =
    get("/{dsId}") {
        val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
        val dataSourcesDao: DataSourcesDao by closestDI().instance()
        val dataSourceWithContacts = dataSourcesDao.getByIdWithContacts(dsId)
        if (dataSourceWithContacts == null) {
            call.respondHtmx {
                addCreateToastEvent("No data source for ds_id = $dsId")
            }
            return@get
        }
        call.respondHtmx {
            pushUrl = "/data-sources/$dsId"
            addHtml {
                dataDisplay(
                    title = "Data Source Details",
                    dataUrl = apiV1Url("$DATA_SOURCE_API_BASE_URL/$dsId"),
                    editUrl = apiV1Url("$DATA_SOURCE_API_BASE_URL/$dsId/edit"),
                ) {
                    dataSourceDisplay(dataSourceWithContacts)
                }
            }
        }
    }

private fun Route.editDataSource() =
    route("/{dsId}/edit") {
        get {
            val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
            val dataSourcesDao: DataSourcesDao by closestDI().instance()
            val recordWarehouseTypesDao: RecordWarehouseTypesDao by closestDI().instance()
            val usersDao: UsersDao by closestDI().instance()
            val workflowsDao: WorkflowsDao by closestDI().instance()
            val dataSource = dataSourcesDao.getById(dsId)
            if (dataSource == null) {
                call.respondHtmx {
                    addCreateToastEvent("No data source for ds_id = $dsId")
                }
                return@get
            }
            val recordWarehouseTypes = recordWarehouseTypesDao.getAll()
            val collectionUsers = usersDao.getWithRole(Role.PipelineCollection)
            val workflows = workflowsDao.getAll()
            call.respondHtmx {
                pushUrl = "/data-sources/$dsId/edit"
                addHtml {
                    dataEdit(
                        title = "Edit Data Source Details",
                        patchUrl = call.request.uri,
                        cancelUrl = apiV1Url("$DATA_SOURCE_API_BASE_URL/${dataSource.dsId}"),
                    ) {
                        dataSourceEdit(
                            dataSource = dataSource,
                            recordWarehouseTypes = recordWarehouseTypes,
                            collectionUsers = collectionUsers,
                            workflows = workflows,
                        )
                    }
                }
            }
        }
        patch {
            val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
            val user = call.userSessionOrRedirect() ?: return@patch
            val request = call.receive<UpdateDateSourceRequest>()
            val dataSourcesDao: DataSourcesDao by closestDI().instance()
            dataSourcesDao.update(user.userId, dsId, request)
            call.respondHtmx {
                addCreateToastEvent("Updated data source, id = $dsId")
                addLoadProxy(apiV1Url("$DATA_SOURCE_API_BASE_URL/$dsId"))
            }
        }
    }

private fun Route.createContactForm() =
    get("/create") {
        val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
        call.respondHtmx {
            pushUrl = "/data-sources/$dsId/contact/create"
            addHtml {
                createDataSourceContactForm(dsId)
            }
        }
    }

private fun Route.createContact() =
    post {
    }
