package com.github.clasicrando.web.api

import com.github.clasicrando.datasources.model.toContactId
import com.github.clasicrando.datasources.model.toDsId
import com.github.clasicrando.requests.CreateDateSourceRequest
import com.github.clasicrando.requests.ModifyDataSourceContactRequest
import com.github.clasicrando.requests.UpdateDateSourceRequest
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.web.component.CreateDataSourceModal
import com.github.clasicrando.web.component.CreateOrUpdateDataSourceContactModal
import com.github.clasicrando.web.component.DataSource
import com.github.clasicrando.web.component.DataSourceContact
import com.github.clasicrando.web.component.DataSourceDisplay
import com.github.clasicrando.web.component.DataSourceEditForm
import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.dataSourceContactsDao
import com.github.clasicrando.web.dataSourcesDao
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.recordWarehouseTypesDao
import com.github.clasicrando.web.userOrRedirect
import com.github.clasicrando.web.userSessionOrRedirect
import com.github.clasicrando.web.userWithRoleOrRespond
import com.github.clasicrando.web.usersDao
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.tbody
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.dataSources() =
    route("/data-sources") {
        getAllDataSources()
        createDataSourceModal()
        createDataSource()
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
        route("/record-warehouse-types") {
            getRecordWarehouseTypes()
        }
    }

private fun Route.getAllDataSources() =
    get {
        userSessionOrRedirect() ?: return@get
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
        val user = userOrRedirect() ?: return@get
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val dataSource = dataSourcesDao.getById(dsId)
        if (dataSource == null) {
            call.respondHtmx {
                addCreateToastEvent("No data source for ds_id = $dsId")
            }
            return@get
        }
        call.respondHtmx {
            addHtml {
                DataSourceDisplay(dataSource, user)
            }
        }
    }

private fun Route.createDataSourceModal() =
    get("/create") {
        val usersDao = usersDao
        call.userWithRoleOrRespond(dao = usersDao, role = Role.CreateDataSource) ?: return@get
        val collectionUsers = usersDao.getWithRole(Role.PipelineCollection)
        call.respondHtmx {
            addHtml {
                CreateDataSourceModal(collectionUsers = collectionUsers)
            }
        }
    }

private fun Route.createDataSource() =
    post {
        val user = userWithRoleOrRespond(Role.CreateDataSource) ?: return@post
        val request = call.receive<CreateDateSourceRequest>()
        request.validate()?.let { issue ->
            call.respondHtmx {
                addModalErrorMessage(issue)
            }
            return@post
        }
        val dsId = dataSourcesDao.create(user.userId, request)
        call.respondHtmx {
            addCreateToastEvent("Created data source, id = $dsId")
            addModalCloseEvent(request.modalId)
            addRefreshDataEvent()
        }
    }

private fun Route.editDataSourceForm() =
    get("/edit") {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val usersDao: UsersDao by closestDI().instance()
        call.userWithRoleOrRespond(dao = usersDao, role = Role.CreateDataSource) ?: return@get
        val dataSource = dataSourcesDao.getById(dsId)
        if (dataSource == null) {
            call.respondHtmx {
                addCreateToastEvent("No data source for ds_id = $dsId")
            }
            return@get
        }
        val collectionUsers = usersDao.getWithRole(Role.PipelineCollection)
        call.respondHtmx {
            addHtml {
                DataSourceEditForm(
                    dataSource = dataSource,
                    collectionUsers = collectionUsers,
                )
            }
        }
    }

private fun Route.editDataSource() =
    patch {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val user = userWithRoleOrRespond(role = Role.EditDataSource) ?: return@patch
        val request = call.receive<UpdateDateSourceRequest>()
        request.validate()?.let { issue ->
            call.respondHtmx {
                addCreateToastEvent("Error: $issue")
            }
            return@patch
        }
        dataSourcesDao.update(user.userId, dsId, request)
        call.respondHtmx {
            addCreateToastEvent("Updated data source, id = $dsId")
            redirect = "/data-sources/$dsId"
        }
    }

private fun Route.contacts() =
    get {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        userSessionOrRedirect() ?: return@get
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
        userWithRoleOrRespond(role = Role.EditDataSource) ?: return@get
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()

        call.respondHtmx {
            addHtml {
                CreateOrUpdateDataSourceContactModal(dsId, null)
            }
        }
    }

private fun Route.editContactModal() =
    get("/edit") {
        userWithRoleOrRespond(role = Role.EditDataSource) ?: return@get
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val contactId = call.parameters.getOrFail<Long>("contactId").toContactId()

        val contact = dataSourceContactsDao.getById(contactId)
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
        userWithRoleOrRespond(role = Role.EditDataSource) ?: return@put
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val request = call.receive<ModifyDataSourceContactRequest>()

        request.validate()?.let { errorMessage ->
            call.respondHtmx {
                addModalErrorMessage(errorMessage)
            }
            return@put
        }

        val message =
            if (request.contactId != null) {
                val contactId = request.contactId!!
                dataSourceContactsDao.update(contactId, dsId, request)
                "Updated data source contact, contact_id = $contactId"
            } else {
                dataSourceContactsDao.create(dsId, request)
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
        userWithRoleOrRespond(role = Role.EditDataSource) ?: return@delete
        val contactId = call.parameters.getOrFail<Long>("contactId").toContactId()
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()

        dataSourceContactsDao.delete(contactId, dsId)

        call.respondHtmx {
            addCreateToastEvent("Deleted data source contact, contact_id = $contactId")
            addRefreshDataEvent()
        }
    }

private fun Route.getRecordWarehouseTypes() =
    get {
        val recordWarehouseTypes = recordWarehouseTypesDao.getAll()

        val selectedType =
            call.parameters["current"]
                ?.takeIf { it.isNotBlank() }
                ?: recordWarehouseTypes.first().name
        call.respondHtmx {
            addHtml {
                for (recordWarehouseType in recordWarehouseTypes) {
                    SimpleOption(
                        value = recordWarehouseType.id.toString(),
                        text = recordWarehouseType.name,
                        selected = selectedType == recordWarehouseType.name,
                    )
                }
            }
        }
    }
