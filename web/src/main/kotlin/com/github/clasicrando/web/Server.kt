package com.github.clasicrando.web

import com.github.clasicrando.di.bindDaoComponents
import com.github.clasicrando.logging.logger
import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.web.api.api
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.component.loginForm
import com.github.clasicrando.web.di.bindRedisSessionComponent
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.htmx.respondHtmxLocation
import com.github.clasicrando.web.page.pages
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
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.snappy.SnappyMapper
import java.time.Instant

private val serverLogger by logger()
private val mutex = Mutex()
private val errorLoopCheck = mutableMapOf<String, Instant>()

private fun loopCountStop(
    currentUrl: String,
    errorInstant: Instant,
): Boolean {
    val lastErrorInstant = errorLoopCheck[currentUrl]
    if (lastErrorInstant == null) {
        errorLoopCheck[currentUrl] = errorInstant
        return false
    }

    errorLoopCheck[currentUrl] = errorInstant
    return lastErrorInstant.isAfter(errorInstant.plusSeconds(-1))
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private fun StatusPagesConfig.configure() {
    exception<Throwable> { call, cause ->
        serverLogger.atError {
            message = "Unhandled error at ${call.request.uri}"
            this.cause = cause
        }
        if (call.request.header("HX-Request") == "true") {
            val currentUrl = call.request.header("HX-Current-URL")
            val shouldShortCircuit =
                currentUrl?.let {
                    mutex.withLock {
                        loopCountStop(currentUrl, Instant.now())
                    }
                } ?: false
            call.respondHtmx {
                addCreateToastEvent("Error: ${cause.message}")
                redirect =
                    if (shouldShortCircuit) {
                        serverLogger.atError {
                            message = "Error short circuit"
                            payload = mapOf("url" to (currentUrl ?: ""))
                        }
                        "/"
                    } else {
                        currentUrl ?: "/"
                    }
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

fun Route.loginPage() {
    get("/login") {
        call.respondBasePage(
            contentUrl = apiV1Url("/login"),
            pageTitle = "Login",
        )
    }
}

fun Route.logoutPage() {
    get("/logout") {
        call.sessions.clear<UserSession>()
        call.respondRedirect(url = "/login")
    }
}

fun Route.apiLoginContent() {
    get(apiV1Url("/login")) {
        call.respondHtmx {
            addHtml {
                loginForm()
            }
        }
    }
}

fun Route.apiLoginAction() {
    post(apiV1Url("/users/login")) {
        val loginRequest = call.receive<LoginRequest>()
        val dao: UsersDao by closestDI().instance()
        val userId = dao.validateUser(loginRequest)
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

@Suppress("UNUSED")
fun Application.module() {
    SnappyMapper.loadCache()
    environment.monitor.subscribe(ApplicationStarted) { application ->
        serverLogger.atInfo {
            message = "Server is starting up"
        }
    }
    environment.monitor.subscribe(ApplicationStopped) { application ->
        serverLogger.atInfo {
            message = "Server is shutting down"
        }
    }
    di {
        bindDaoComponents()
        bindRedisSessionComponent()
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
            pages()
            api()
        }

        loginPage()
        logoutPage()
        apiLoginContent()
        apiLoginAction()
    }
}
