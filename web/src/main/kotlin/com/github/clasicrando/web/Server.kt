package com.github.clasicrando.web

import com.github.clasicrando.di.diContainer
import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.web.api.api
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.htmx.respondHtmxLocation
import com.github.clasicrando.web.page.LoginPage
import com.github.clasicrando.web.page.pages
import com.github.clasicrando.web.page.respondHtmlPage
import com.github.clasicrando.web.session.RedisSessionStorage
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.newClient
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.session
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.snappy.command.sqlCommand
import java.sql.Connection
import java.util.UUID

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("UNUSED")
fun Application.module() {
    di {
        extend(diContainer)
        bindEagerSingleton {
            val redisHost =
                System.getenv("EM_REDIS_URL")
                    ?: error("Could not find EM_REDIS_URL environment variable")
            newClient(Endpoint.from(redisHost))
        }
        bindProvider<SessionStorage> {
            RedisSessionStorage(this.di)
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { it }
            challenge("/login")
        }
    }
    install(Sessions) {
        val sessionStorage: SessionStorage by this@module.closestDI().instance()
        cookie<UserSession>("em_user_session", sessionStorage) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 8
            cookie.httpOnly = true
        }
    }
    routing {
        staticResources("/assets", "assets")
        authenticate("auth-session") {
            pages()
            api()
        }
        get("/login") {
            call.respondHtmlPage(LoginPage())
        }
        post(apiV1Url("/users/login")) {
            val loginRequest = call.receive<LoginRequest>()
            val connection: Connection by closestDI().instance()
            val userId =
                connection.use {
                    sqlCommand("select em.validate_user(?, ?)")
                        .bind(loginRequest.username)
                        .bind(loginRequest.password)
                        .queryScalarOrNullSuspend<UUID>(it)
                }
            if (userId == null) {
                call.respondHtmx {
                    addCreateToastEvent("Invalid username or password")
                }
                return@post
            }
            call.sessions.set(UserSession(userId))
            call.respondHtmxLocation("/")
        }
    }
}
