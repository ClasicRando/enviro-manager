package com.github.clasicrando.web.api

import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.workflows.data.WorkflowsDao
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

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
        call.respondWithWorkflows(state = "Data Collection")
    }

private fun Route.loadWorkflows() =
    get("/load") {
        call.respondWithWorkflows(state = "Data Loading")
    }

private fun Route.checkWorkflows() =
    get("/check") {
        call.respondWithWorkflows(state = "Load Checking")
    }

private fun Route.qaWorkflows() =
    get("/qa") {
        call.respondWithWorkflows(state = "Load QA")
    }

private suspend fun ApplicationCall.respondWithWorkflows(state: String) {
    val workflowsDao: WorkflowsDao by closestDI().instance()
    val workflows =
        workflowsDao
            .getAll()
            .filter { it.pipelineState == state }
    val current =
        parameters["current"]
            ?.takeIf { it.isNotBlank() }
            ?: workflows.first { it.name.contains(other = "default", ignoreCase = true) }.name
    respondHtmx {
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
