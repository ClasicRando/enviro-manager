package com.github.clasicrando.web.api

import com.github.clasicrando.models.DataSource
import com.github.clasicrando.web.component.dataSourceRow
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.snappy.command.sqlCommand
import java.net.URLEncoder
import java.sql.Connection

private const val API_V1_PREFIX = "/api/v1"

fun apiV1Url(
    extra: String,
    queryParams: Map<String, String> = mapOf(),
): String =
    buildString {
        append(API_V1_PREFIX)
        append('/')
        append(extra.trim('/'))
        for ((key, value) in queryParams) {
            append(key)
            append('=')
            append(URLEncoder.encode(value, Charsets.UTF_8))
        }
    }

fun Route.api() =
    route(API_V1_PREFIX) {
        dataSources()
    }

private fun Route.dataSources() =
    route("/data-sources") {
        get {
            val connection: Connection by closestDI().instance()
            val dataSources =
                connection.use {
                    sqlCommand("select * from em.v_data_sources")
                        .querySuspend<DataSource>(connection)
                        .toList()
                }
            call.respondHtmx {
                addHtml {
                    for (ds in dataSources) {
                        dataSourceRow(ds)
                    }
                }
            }
        }
    }
