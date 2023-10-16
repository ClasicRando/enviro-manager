package com.github.clasicrando.web

import com.github.clasicrando.models.User
import com.github.clasicrando.web.page.home
import io.ktor.server.application.*
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.*
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("UNUSED")
fun Application.module() {
    routing {
        index()
        staticResources("/assets", "assets")
    }
}

fun Route.index() {
    get("/") {
        call.respondHtml {
            home(User(
                UUID.randomUUID(),
                "thomsons",
                "Steven Thomson",
                emptyList(),
            ))
        }
    }
}
