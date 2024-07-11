package com.github.clasicrando.web.api

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.model.toContactId
import com.github.clasicrando.datasources.model.toDsId
import com.github.clasicrando.requests.ModifyDataSourceContactRequest
import com.github.clasicrando.requests.UpdateDateSourceRequest
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.web.component.CreateOrUpdateDataSourceContactModal
import com.github.clasicrando.web.component.DataSource
import com.github.clasicrando.web.component.DataSourceContact
import com.github.clasicrando.web.component.DataSourceDisplay
import com.github.clasicrando.web.component.DataSourceEditForm
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.userSessionOrRedirect
import com.github.clasicrando.workflows.data.WorkflowsDao
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.tbody
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
                contacts()
                createContactModal()
                createOrEditContact()
                route("/{contactId}") {
                    editContactModal()
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
                tbody {
                    for (dataSource in dataSources) {
                        DataSource(dataSource)
                    }
                }
            }
        }
    }

private fun Route.getDataSource() =
    get {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val dataSourcesDao: DataSourcesDao by closestDI().instance()
        val dataSource = dataSourcesDao.getById(dsId)
        if (dataSource == null) {
            call.respondHtmx {
                addCreateToastEvent("No data source for ds_id = $dsId")
            }
            return@get
        }
        call.respondHtmx {
            addHtml {
                DataSourceDisplay(dataSource)
            }
        }
    }

private fun Route.editDataSourceForm() =
    get("/edit") {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
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
            addHtml {
                DataSourceEditForm(
                    dataSource = dataSource,
                    recordWarehouseTypes = recordWarehouseTypes,
                    collectionUsers = collectionUsers,
                    workflows = workflows,
                )
            }
        }
    }

private fun Route.editDataSource() =
    patch {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val user = call.userSessionOrRedirect() ?: return@patch
        val request = call.receive<UpdateDateSourceRequest>()
        request.validate()?.let { issue ->
            call.respondHtmx {
                addCreateToastEvent("Error: $issue")
            }
            return@patch
        }
        val dataSourcesDao: DataSourcesDao by closestDI().instance()
        dataSourcesDao.update(user.userId, dsId, request)
        call.respondHtmx {
            addCreateToastEvent("Updated data source, id = $dsId")
            redirect = "/data-sources/$dsId"
        }
    }

private fun Route.contacts() =
    get {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        call.userSessionOrRedirect() ?: return@get
        val dataSourceContactsDao: DataSourceContactsDao by closestDI().instance()
        val contacts = dataSourceContactsDao.getByDsId(dsId)
        call.respondHtmx {
            addHtml {
                tbody {
                    for (contact in contacts) {
                        DataSourceContact(contact)
                    }
                }
            }
        }
    }

private fun Route.createContactModal() =
    get("/create") {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()

        call.respondHtmx {
            addHtml {
                CreateOrUpdateDataSourceContactModal(dsId, null)
            }
        }
    }

private fun Route.editContactModal() =
    get("/edit") {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val contactId = call.parameters.getOrFail<Long>("contactId").toContactId()
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
                CreateOrUpdateDataSourceContactModal(dsId, contact)
            }
        }
    }

private fun Route.createOrEditContact() =
    put {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val request = call.receive<ModifyDataSourceContactRequest>()

        request.validate()?.let { errorMessage ->
            call.respondHtmx {
                addModalErrorMessage(errorMessage)
            }
            return@put
        }

        val dao: DataSourceContactsDao by closestDI().instance()

        val message =
            if (request.contactId != null) {
                val contactId = request.contactId!!
                dao.update(contactId, dsId, request)
                "Updated data source contact, contact_id = $contactId"
            } else {
                dao.create(dsId, request)
                "Created data source contact"
            }

        call.respondHtmx {
            addModalCloseEvent(request.modalId)
            addCreateToastEvent(message)
            addRefreshDataEvent()
        }
    }

private fun Route.deleteContact() =
    delete {
        val contactId = call.parameters.getOrFail<Long>("contactId").toContactId()
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val dao: DataSourceContactsDao by closestDI().instance()

        dao.delete(contactId, dsId)

        call.respondHtmx {
            addCreateToastEvent("Deleted data source contact, contact_id = $contactId")
            addRefreshDataEvent()
        }
    }
