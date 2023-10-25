package com.github.clasicrando.web.api

import io.ktor.server.routing.Route
import io.ktor.server.routing.route
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

fun Route.api() =
    route(API_V1_PREFIX) {
        users()
        dataSources()
    }
