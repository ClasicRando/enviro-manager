package com.github.clasicrando.web.api

import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.users() =
    route("/users") {
    }
