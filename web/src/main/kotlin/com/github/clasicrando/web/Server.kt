package com.github.clasicrando.web

import com.github.clasicrando.di.bindDaoComponents
import com.github.clasicrando.di.cleanUpResources
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.web.api.authenticatedApi
import com.github.clasicrando.web.api.unauthenticatedApi
import com.github.clasicrando.web.di.bindRedisSessionComponent
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.page.authenticatedPages
import com.github.clasicrando.web.page.unauthenticatedPages
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.session
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.header
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di

private val serverLogger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain
        .main(args)

private fun StatusPagesConfig.configure() {
    exception<Throwable> { call, cause ->
        serverLogger.atError {
            message = "Unhandled error at ${call.request.uri}"
            this.cause = cause
        }
        if (call.request.header("HX-Request") == "true") {
            call.respondHtmx {
                addCreateToastEvent("Error: ${cause.message}")
            }
            return@exception
        }
        call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
    }
}

private fun AuthenticationConfig.configureSession() {
    session<UserSession>("auth-session") {
        validate {
            val dao: UsersDao by closestDI().instance()
            val user = dao.getById(it.userId)
            if (user == null) {
                null
            } else {
                it
            }
        }
        challenge("/login")
    }
}

fun Route.shutdownServer() {
    val shutdown = ShutDownUrl(url = "") { 0 }
    post("/admin-shutdown") {
        val usersDao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(usersDao) ?: return@post

        if (!user.hasRole(Role.Admin)) {
            call.respondText(
                text = "You tried to shutdown the server without admin rights. Naughty, naughty",
                status = HttpStatusCode.Forbidden,
            )
            return@post
        }

        serverLogger.atInfo {
            message = "Admin user '${user.username}' sent a shutdown request"
        }

        /**
         * Code below is taken from shutdown url plugin, but I needed to add the user role check
         * https://ktor.io/docs/shutdown-url.html
         */
        shutdown.doShutdown(call)
    }
}

@Suppress("UNUSED")
fun Application.module() {
    di {
        bindDaoComponents()
        bindRedisSessionComponent()
    }
    environment.monitor.subscribe(ApplicationStarted) {
        serverLogger.atInfo {
            message = "Server is starting up"
        }
    }
    environment.monitor.subscribe(ApplicationStopped) {
        val di by closestDI()
        runBlocking { di.cleanUpResources() }
        serverLogger.atInfo {
            message = "Server is shutting down"
        }
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            },
        )
    }
    install(StatusPages) {
        configure()
    }
    install(Authentication) {
        configureSession()
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
            authenticatedPages()
            authenticatedApi()
            shutdownServer()
        }

        unauthenticatedPages()
        unauthenticatedApi()
    }
}
