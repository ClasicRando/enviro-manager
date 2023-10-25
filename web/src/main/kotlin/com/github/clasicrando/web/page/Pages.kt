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
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.snappy.command.sqlCommand
import java.sql.Connection

suspend fun <T : BasePage> ApplicationCall.respondHtmlPage(page: T) {
    respondHtmlTemplate(page) {}
}

fun Route.pages() {
    index()
}

private fun Route.index() =
    get("/") {
        val userSession = call.sessions.get<UserSession>()!!
        val connection: Connection by closestDI().instance()
        val user =
            connection.use {
                sqlCommand(
                    """
                    select u.user_id, u.username, u.full_name, u.roles
                    from em.v_users u
                    where u.user_id = ?
                    """.trimIndent(),
                )
                    .bind(userSession.userId)
                    .queryFirstSuspend<User>(it)
            }
        call.respondHtmlTemplate(Home(user)) {}
    }
