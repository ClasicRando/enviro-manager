package com.github.clasicrando.web.api

import com.github.clasicrando.requests.CreateOrUpdateWorkflowRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.web.component.CreateOrUpdateWorkflowModal
import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.component.Workflow
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.userWithRoleOrRespond
import com.github.clasicrando.web.workflowsDao
import com.github.clasicrando.workflows.model.WorkflowIdJson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import kotlinx.html.tbody

fun Route.workflows() {
    route("/workflows") {
        getWorkflows()
        collectionWorkflows()
        loadWorkflows()
        checkWorkflows()
        qaWorkflows()
        createWorkflowModal()
        updateWorkflowModal()
        createOrUpdateWorkflow()
    }
}

private fun Route.getWorkflows() =
    get {
        val workflows = workflowsDao.getAll()
        call.respondHtmx {
            addHtml {
                tbody {
                    for (workflow in workflows) {
                        Workflow(workflow)
                    }
                }
            }
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

private fun Route.createWorkflowModal() =
    get("/create") {
        userWithRoleOrRespond(Role.Developer) ?: return@get
        call.respondHtmx {
            addHtml {
                CreateOrUpdateWorkflowModal(workflow = null)
            }
        }
    }

private fun Route.updateWorkflowModal() =
    post("/edit") {
        userWithRoleOrRespond(Role.Developer) ?: return@post
        val json = call.receive<WorkflowIdJson>()
        val workflow = workflowsDao.getById(json.workflowId)
        call.respondHtmx {
            addHtml {
                CreateOrUpdateWorkflowModal(workflow)
            }
        }
    }

private fun Route.createOrUpdateWorkflow() =
    put {
        userWithRoleOrRespond(Role.Developer) ?: return@put
        val request = call.receive<CreateOrUpdateWorkflowRequest>()
        val message =
            if (request.workflowId == null) {
                workflowsDao.createWorkflow(
                    name = request.name,
                    workflowDefinitionName = request.workflowDefinitionName,
                    pipelineState = request.pipelineState,
                )
                "Create new workflow"
            } else {
                workflowsDao.updateWorkflow(
                    workflowId = request.workflowId!!,
                    name = request.name,
                    workflowDefinitionName = request.workflowDefinitionName,
                    pipelineState = request.pipelineState,
                )
                "Updated existing workflow"
            }

        call.respondHtmx {
            addCreateToastEvent(message)
            addModalCloseEvent(request.modalId)
            addRefreshDataEvent()
        }
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
