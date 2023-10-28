package com.github.clasicrando.web

import com.github.clasicrando.datasources.DataSourcesDao
import com.github.clasicrando.datasources.PgDataSourcesDao
import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.PgUsersDao
import com.github.clasicrando.users.UsersDao
import com.github.clasicrando.web.api.api
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.component.loginForm
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.htmx.respondHtmxLocation
import com.github.clasicrando.web.page.pages
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
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.apache.commons.dbcp2.BasicDataSource
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import org.postgresql.PGConnection
import org.snappy.SnappyMapper
import javax.sql.DataSource

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("UNUSED")
fun Application.module() {
    SnappyMapper.loadCache()
    di {
        bindEagerSingleton<DataSource> {
            BasicDataSource().apply {
                defaultAutoCommit = true
                url = System.getenv("EM_DB_URL")
            }
        }
        bindProvider<PGConnection> {
            val dataSource: DataSource by di.instance()
            dataSource.connection.unwrap(PGConnection::class.java)
        }
        bindEagerSingleton {
            val redisHost =
                System.getenv("EM_REDIS_URL")
                    ?: error("Could not find EM_REDIS_URL environment variable")
            newClient(Endpoint.from(redisHost))
        }
        bindProvider<SessionStorage> {
            RedisSessionStorage(di)
        }
        bindProvider<DataSourcesDao> {
            PgDataSourcesDao(di)
        }
        bindProvider<UsersDao> {
            PgUsersDao(di)
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
            call.respondBasePage(
                contentUrl = apiV1Url("/login"),
                pageTitle = "Login",
            )
        }
        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect(url = "/login")
        }
        get(apiV1Url("/login")) {
            call.respondHtmx {
                addHtml {
                    loginForm()
                }
            }
        }
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
}
