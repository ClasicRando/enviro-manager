package com.github.clasicrando.web.page

import com.github.clasicrando.models.User
import io.ktor.server.application.call
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.util.UUID

fun Route.pages() {
    index()
}

private fun Route.index() =
    get("/") {
        val user =
            User(
                UUID.randomUUID(),
                "thomsons",
                "Steven Thomson",
                emptyList(),
            )
        call.respondHtmlTemplate(Home(user)) {}
    }
