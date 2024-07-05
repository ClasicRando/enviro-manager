package com.github.clasicrando.web.api

import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.web.UserSession
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.net.URLEncoder

private const val API_V1_PREFIX = "/api/v1"

fun apiV1Url(
    extra: String,
    queryParams: Map<String, String> = mapOf(),
): String =
    buildString {
        append(API_V1_PREFIX)
        append('/')
        append(extra.trim('/'))
        for ((key, value) in queryParams) {
            append(key)
            append('=')
            append(URLEncoder.encode(value, Charsets.UTF_8))
        }
    }

fun Route.unauthenticatedApi() =
    route(API_V1_PREFIX) {
        post("/users/login") {
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
            call.respondHtmx {
                redirect = "/"
            }
        }
    }

fun Route.authenticatedApi() =
    route(API_V1_PREFIX) {
        users()
        dataSources()
    }
