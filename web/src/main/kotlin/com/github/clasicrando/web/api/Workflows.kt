package com.github.clasicrando.web.api

import com.github.clasicrando.requests.CreateOrUpdateWorkflowRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.web.component.CreateOrUpdateWorkflowModal
import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.component.Workflow
import com.github.clasicrando.web.component.WorkflowTask
import com.github.clasicrando.web.component.WorkflowTaskRow
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.userWithRoleOrRespond
import com.github.clasicrando.web.workflowTasksDao
import com.github.clasicrando.web.workflowsDao
import com.github.clasicrando.workflows.model.WorkflowTaskComposite
import com.github.clasicrando.workflows.model.toWorkflowId
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.tbody
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

fun Route.workflows() {
    route("/workflows") {
        getWorkflows()
        workflowOptions()
        createWorkflowModal()
        updateWorkflowModal()
        createOrUpdateWorkflow()
        getWorkflowTasks()
        addTaskRow()
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

private fun Route.getWorkflowTasks() =
    get("/tasks/{workflowId}") {
        val workflowId = call.parameters
            .getOrFail("workflowId")
            .toInt()
            .toWorkflowId()
        val edit = call.parameters["edit"]?.toBoolean() ?: false
        val workflowTasks = workflowTasksDao.getWorkflowTasks(workflowId)
        call.respondHtmx {
            addHtml {
                if (edit) {
                    for (workflowTask in workflowTasks) {
                        WorkflowTaskRow(workflowTask)
                    }
                } else {
                    tbody {
                        for (workflowTask in workflowTasks) {
                            WorkflowTask(workflowTask)
                        }
                    }
                }
            }
        }
    }

private fun Route.workflowOptions() =
    get("/options") {
        val workflows = workflowsDao.getAll()
        val current =
            call.parameters["current"]
                ?.takeIf { it.isNotBlank() }
                ?: workflows.first { it.name.contains(other = "default", ignoreCase = true) }.name
        call.respondHtmx {
            addHtml {
                for (workflow in workflows) {
                    SimpleOption(
                        value = workflow.workflowId.toString(),
                        text = workflow.name,
                        selected = workflow.name == current,
                    )
                }
            }
        }
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
    get("/edit/{workflowId}") {
        userWithRoleOrRespond(Role.Developer) ?: return@get
        val workflowId = call.parameters
            .getOrFail("workflowId")
            .toInt()
            .toWorkflowId()
        val workflow = workflowsDao.getById(workflowId)
        call.respondHtmx {
            addHtml {
                CreateOrUpdateWorkflowModal(workflow)
            }
        }
    }

private fun Route.addTaskRow() =
    get("/add-task") {
        call.respondHtmx {
            addHtml {
                WorkflowTaskRow(workflowTask = null)
            }
        }
    }

private fun Route.createOrUpdateWorkflow() =
    put {
        userWithRoleOrRespond(Role.Developer) ?: return@put
        val request = call.receive<CreateOrUpdateWorkflowRequest>()
        request.validate()?.let { issue ->
            call.respondHtmx {
                addModalErrorMessage(issue)
            }
            return@put
        }

        val workflowTasks = request.taskOrders
            .withIndex()
            .map { (i, order) ->
                val jsonLiteral = request.defaultInputParameters[i]
                    .takeIf { it is JsonPrimitive && it.isString }
                    ?.jsonPrimitive
                    ?.content
                    ?.let { Json.decodeFromString<JsonObject>(it) }
                WorkflowTaskComposite(
                    workflowId = request.workflowId?.value ?: 0,
                    taskOrder = order,
                    taskId = request.taskIds[i].value,
                    defaultInputParameters = jsonLiteral,
                )
            }
        val message =
            if (request.workflowId == null) {
                workflowsDao.createWorkflow(
                    name = request.name,
                    workflowTasks = workflowTasks,
                )
                "Create new workflow"
            } else {
                workflowsDao.updateWorkflow(
                    workflowId = request.workflowId!!,
                    name = request.name,
                    workflowTasks = workflowTasks,
                )
                "Updated existing workflow"
            }

        call.respondHtmx {
            addCreateToastEvent(message)
            addModalCloseEvent(request.modalId)
            addRefreshDataEvent()
        }
    }
