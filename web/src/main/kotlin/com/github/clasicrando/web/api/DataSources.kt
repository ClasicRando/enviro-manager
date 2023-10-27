package com.github.clasicrando.web.api

import com.github.clasicrando.datasources.DataSourcesDao
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.web.component.dataSourceDisplay
import com.github.clasicrando.web.component.dataSourceRow
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.dataSources() =
    route("/data-sources") {
        getAllDataSources()
        getDataSource()
    }

private fun Route.getAllDataSources() =
    get {
        val dataSourcesDao: DataSourcesDao by closestDI().instance()
        val dataSources = dataSourcesDao.getAll()
        call.respondHtmx {
            addHtml {
                for (ds in dataSources) {
                    dataSourceRow(ds)
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
                dataSourceDisplay(dataSourceWithContacts)
            }
        }
    }
