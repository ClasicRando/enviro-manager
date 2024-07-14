package com.github.clasicrando.web.api

import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.workflowsDao
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext

fun Route.workflows() {
    route("/workflows") {
        collectionWorkflows()
        loadWorkflows()
        checkWorkflows()
        qaWorkflows()
    }
}

private fun Route.collectionWorkflows() =
    get("/collection") {
        respondWithWorkflows(state = "Data Collection")
    }

private fun Route.loadWorkflows() =
    get("/load") {
        respondWithWorkflows(state = "Data Loading")
    }

private fun Route.checkWorkflows() =
    get("/check") {
        respondWithWorkflows(state = "Load Checking")
    }

private fun Route.qaWorkflows() =
    get("/qa") {
        respondWithWorkflows(state = "Load QA")
    }

private suspend fun PipelineContext<Unit, ApplicationCall>.respondWithWorkflows(state: String) {
    val workflows =
        workflowsDao
            .getAll()
            .filter { it.pipelineState == state }
    val current =
        call.parameters["current"]
            ?.takeIf { it.isNotBlank() }
            ?: workflows.first { it.name.contains(other = "default", ignoreCase = true) }.name
    call.respondHtmx {
        addHtml {
            for (workflow in workflows) {
                SimpleOption(
                    value = workflow.id.toString(),
                    text = workflow.name,
                    selected = workflow.name == current,
                )
            }
        }
    }
}
