package com.github.clasicrando.web.page

import com.github.clasicrando.users.UsersDao
import com.github.clasicrando.web.UserSession
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.util.getOrFail
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

suspend fun <T : BasePage> ApplicationCall.respondHtmlPage(page: T) {
    respondHtmlTemplate(page) {}
}

fun Route.pages() {
    index()
    dataSources()
    dataSource()
}

private fun Route.index() =
    get("/") {
        val userSession = call.sessions.get<UserSession>()!!
        val dao: UsersDao by closestDI().instance()
        val user = dao.getById(userId = userSession.userId)
        if (user == null) {
            call.respondRedirect("/login")
            return@get
        }
        call.respondHtmlPage(Home(user = user))
    }

private fun Route.dataSources() =
    get("/data-sources") {
        val userSession = call.sessions.get<UserSession>()!!
        val dao: UsersDao by closestDI().instance()
        val user = dao.getById(userId = userSession.userId)
        if (user == null) {
            call.respondRedirect("/login")
            return@get
        }
        call.respondHtmlPage(DataSources(user = user))
    }

private fun Route.dataSource() =
    get("/data-sources/{dsId}") {
        val dsId = call.parameters.getOrFail<Long>("dsId")
        val userSession = call.sessions.get<UserSession>()!!
        val dao: UsersDao by closestDI().instance()
        val user = dao.getById(userId = userSession.userId)
        if (user == null) {
            call.respondRedirect("/login")
            return@get
        }
        call.respondHtmlPage(DataSource(user = user, dsId = dsId))
    }
