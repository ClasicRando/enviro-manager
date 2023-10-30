package com.github.clasicrando.web.api

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.model.ContactId
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.requests.ModifyDataSourceContactRequest
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
import com.github.clasicrando.web.component.editDataSourceContactForm
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.userSessionOrRedirect
import com.github.clasicrando.workflows.data.WorkflowsDao
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.th
import kotlinx.html.tr
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.dataSources() =
    route("/data-sources") {
        getAllDataSources()
        route("/{dsId}") {
            getDataSource()
            editDataSourceForm()
            editDataSource()
            route("/contacts") {
                createContactForm()
                createContact()
                route("/{contactId}") {
                    editContactForm()
                    editContact()
                    deleteContact()
                }
            }
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
    get {
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
                    dataUrl = apiV1Url("/data-sources/$dsId"),
                    editUrl = apiV1Url("/data-sources/$dsId/edit"),
                ) {
                    dataSourceDisplay(dataSourceWithContacts)
                }
            }
        }
    }

private fun Route.editDataSourceForm() =
    get("/edit") {
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
                val url = apiV1Url("/data-sources/${dataSource.dsId}")
                dataEdit(
                    title = "Edit Data Source Details",
                    patchUrl = url,
                    cancelUrl = url,
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

private fun Route.editDataSource() =
    patch {
        val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
        val user = call.userSessionOrRedirect() ?: return@patch
        val request = call.receive<UpdateDateSourceRequest>()
        val dataSourcesDao: DataSourcesDao by closestDI().instance()
        dataSourcesDao.update(user.userId, dsId, request)
        call.respondHtmx {
            addCreateToastEvent("Updated data source, id = $dsId")
            addLoadProxy(apiV1Url("/data-sources/$dsId"))
        }
    }

private fun Route.createContactForm() =
    get("/create") {
        val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
        call.respondHtmx {
            pushUrl = "/data-sources/$dsId/contacts/create"
            addHtml {
                createDataSourceContactForm(dsId)
            }
        }
    }

private fun Route.createContact() =
    post {
        val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
        val request = call.receive<ModifyDataSourceContactRequest>()
        val dao: DataSourceContactsDao by closestDI().instance()

        dao.create(dsId, request)

        call.respondHtmx {
            addCreateToastEvent("Created new data source contact")
            addLoadProxy(apiV1Url("/data-sources/$dsId"))
        }
    }

private fun Route.editContactForm() =
    get("/edit") {
        val contactId = ContactId(call.parameters.getOrFail<Long>("contactId"))
        val dao: DataSourceContactsDao by closestDI().instance()

        val contact = dao.getById(contactId)
        if (contact == null) {
            call.respondHtmx {
                addCreateToastEvent("No contact for contact_id = $contactId")
            }
            return@get
        }

        call.respondHtmx {
            addHtml {
                editDataSourceContactForm(contact)
            }
        }
    }

private fun Route.editContact() =
    patch {
        val contactId = ContactId(call.parameters.getOrFail<Long>("contactId"))
        val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
        val request = call.receive<ModifyDataSourceContactRequest>()
        val dao: DataSourceContactsDao by closestDI().instance()

        dao.update(contactId, dsId, request)

        call.respondHtmx {
            addCreateToastEvent("Updated data source contact, contact_id = $contactId")
            addLoadProxy(apiV1Url("/data-sources/$dsId"))
        }
    }

private fun Route.deleteContact() =
    delete {
        val contactId = ContactId(call.parameters.getOrFail<Long>("contactId"))
        val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
        val dao: DataSourceContactsDao by closestDI().instance()

        dao.delete(contactId, dsId)

        call.respondHtmx {
            addCreateToastEvent("Delete data source contact, contact_id = $contactId")
            addLoadProxy(apiV1Url("/data-sources/$dsId"))
        }
    }
