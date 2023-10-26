package com.github.clasicrando.web.page

import com.github.clasicrando.models.User
import com.github.clasicrando.web.UserSession
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.util.getOrFail
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.snappy.command.sqlCommand
import java.sql.Connection
import java.util.UUID

suspend fun <T : BasePage> ApplicationCall.respondHtmlPage(page: T) {
    respondHtmlTemplate(page) {}
}

fun Route.pages() {
    index()
    dataSources()
    dataSource()
}

private suspend fun getUser(
    connection: Connection,
    userId: UUID,
): User {
    return connection.use {
        sqlCommand(
            """
            select u.user_id, u.username, u.full_name, u.roles
            from em.v_users u
            where u.user_id = ?
            """.trimIndent(),
        )
            .bind(userId)
            .queryFirstSuspend<User>(it)
    }
}

private fun Route.index() =
    get("/") {
        val userSession = call.sessions.get<UserSession>()!!
        val connection: Connection by closestDI().instance()
        val user = getUser(connection = connection, userId = userSession.userId)
        call.respondHtmlPage(Home(user = user))
    }

private fun Route.dataSources() =
    get("/data-sources") {
        val userSession = call.sessions.get<UserSession>()!!
        val connection: Connection by closestDI().instance()
        val user = getUser(connection = connection, userId = userSession.userId)
        call.respondHtmlPage(DataSources(user = user))
    }

private fun Route.dataSource() =
    get("/data-sources/{dsId}") {
        val dsId = call.parameters.getOrFail<Long>("dsId")
        val userSession = call.sessions.get<UserSession>()!!
        val connection: Connection by closestDI().instance()
        val user = getUser(connection = connection, userId = userSession.userId)
        call.respondHtmlPage(DataSource(user = user, dsId = dsId))
    }
