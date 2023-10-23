package com.github.clasicrando.web

import com.github.clasicrando.di.diContainer
import com.github.clasicrando.web.api.api
import com.github.clasicrando.web.page.pages
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing
import org.kodein.di.ktor.di

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("UNUSED")
fun Application.module() {
    di {
        extend(diContainer)
    }
    routing {
        staticResources("/assets", "assets")
        pages()
        api()
    }
}
