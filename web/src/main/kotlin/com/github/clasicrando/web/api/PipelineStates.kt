package com.github.clasicrando.web.api

import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.pipelineStatesDao
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.pipelineStates() =
    route("/pipeline-states") {
        getPipelineStates()
    }

private fun Route.getPipelineStates() =
    get {
        val includeDone = call.parameters["includeDone"]?.toBoolean() ?: true
        val pipelineStates =
            pipelineStatesDao
                .getAll()
                .filter { includeDone || it.code != "done" }
        val selectedIndex =
            call.parameters["current"]
                ?.let { current -> pipelineStates.indexOfFirst { it.name == current } }
                ?.coerceAtLeast(0)
                ?: 0
        call.respondHtmx {
            addHtml {
                for ((i, pipelineState) in pipelineStates.withIndex()) {
                    SimpleOption(
                        value = pipelineState.code,
                        text = pipelineState.name,
                        selected = i == selectedIndex,
                    )
                }
            }
        }
    }
