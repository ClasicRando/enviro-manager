package com.github.clasicrando.web

import com.github.clasicrando.di.diContainer
import com.github.clasicrando.models.DataSource
import com.github.clasicrando.models.User
import com.github.clasicrando.web.component.dataSource
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.page.Home
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.kodein.di.instance
import org.snappy.command.sqlCommand
import java.sql.Connection
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("UNUSED")
fun Application.module() {
    routing {
        index()
        staticResources("/assets", "assets")
        api()
    }
}

fun Route.index() = route("/") {
    get {
        val user = User(
            UUID.randomUUID(),
            "thomsons",
            "Steven Thomson",
            emptyList(),
        )
        call.respondHtmlTemplate(Home(user)) {}
    }
}

fun Route.api() = route("/api") {
    apiDataSources()
}

fun Route.apiDataSources() = route("/data-sources") {
    get {
        val connection: Connection by diContainer.instance()
        val dataSources = sqlCommand("select * from em.v_data_sources")
            .querySuspend<DataSource>(connection)
        call.respondHtmx {
            addHtml {
                for (ds in dataSources) {
                    dataSource(ds)
                }
            }
        }
    }
}
