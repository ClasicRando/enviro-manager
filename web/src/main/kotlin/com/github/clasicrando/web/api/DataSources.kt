package com.github.clasicrando.web.api

import com.github.clasicrando.datasources.DataSourcesDao
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.web.component.dataDisplay
import com.github.clasicrando.web.component.dataEdit
import com.github.clasicrando.web.component.dataSourceDisplay
import com.github.clasicrando.web.component.dataSourceRow
import com.github.clasicrando.web.component.dataTable
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.request.uri
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.th
import kotlinx.html.tr
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.dataSources() =
    route("/data-sources") {
        getAllDataSources()
        getDataSource()
        editDataSource()
    }

private fun Route.getAllDataSources() =
    get {
        val dataSourcesDao: DataSourcesDao by closestDI().instance()
        val dataSources = dataSourcesDao.getAll()
        call.respondHtmx {
            addHtml {
                dataTable(
                    caption = "Data Sources",
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
            addHtml {
                dataDisplay(
                    title = "Data Source Details",
                    dataUrl = apiV1Url("/data-sources/$dsId"),
                    editUrl = apiV1Url("/data-sources/edit/$dsId"),
                ) {
                    dataSourceDisplay(dataSourceWithContacts)
                }
            }
        }
    }

private fun Route.editDataSource() =
    route("/edit/{dsId}") {
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
            val pathUrl = apiV1Url("/data-sources/edit/${dataSourceWithContacts.dataSource.dsId}")
            val cancelUrl = apiV1Url("/data-sources/${dataSourceWithContacts.dataSource.dsId}")
            call.respondHtmx {
                addHtml {
                    dataEdit(
                        editId = dataSourceWithContacts.dataSource.dsId.toString(),
                        title = "Edit Data Source Details",
                        patchUrl = pathUrl,
                        cancelUrl = cancelUrl,
                    ) {
                        dataSourceDisplay(data = dataSourceWithContacts, edit = true)
                    }
                }
            }
        }
        patch {
            val dsId = DsId(call.parameters.getOrFail<Long>("dsId"))
        }
    }
