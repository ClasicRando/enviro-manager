package com.github.clasicrando.web.page

import com.github.clasicrando.users.UsersDao
import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.UserSession
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.respondBasePage
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.util.getOrFail
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.pages() {
    index()
    dataSources()
    dataSource()
}

private suspend fun ApplicationCall.userOrRedirect(dao: UsersDao): User? {
    val userSession = sessions.get<UserSession>()!!
    val user = dao.getById(userId = userSession.userId)
    if (user == null) {
        respondRedirect("/login")
        return null
    }
    return user
}

private fun Route.index() =
    get("/") {
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        call.respondBasePage(
            contentUrl = apiV1Url("/home"),
            user = user,
            pageTitle = "Home",
        )
    }

private fun Route.dataSources() =
    get("/data-sources") {
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        call.respondBasePage(
            contentUrl = apiV1Url("/data-sources"),
            user = user,
            pageTitle = "Data Sources",
        )
    }

private fun Route.dataSource() =
    get("/data-sources/{dsId}") {
        val dsId = call.parameters.getOrFail<Long>("dsId")
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        call.respondBasePage(
            contentUrl = apiV1Url("/data-sources/$dsId"),
            user = user,
            pageTitle = "Data Source",
        )
    }
