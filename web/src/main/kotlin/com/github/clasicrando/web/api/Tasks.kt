package com.github.clasicrando.web.api

import com.github.clasicrando.requests.CreateOrUpdateTaskRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.web.component.CreateOrUpdateTask
import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.component.TaskRow
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.tasksDao
import com.github.clasicrando.web.userWithRoleOrRespond
import com.github.clasicrando.workflows.model.toTaskId
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.tbody

fun Route.tasks() {
    route("/tasks") {
        getTasks()
        createTaskModal()
        editTaskModal()
        upsertTask()
    }
}

private fun Route.getTasks() =
    get {
        val tasks = tasksDao.getTasks()
        val isTable = call.parameters["table"]
            ?.takeIf { it.isNotBlank() }
            ?.toBoolean()
            ?: true
        val current = call.parameters["current"]
            ?.takeIf { it.isNotBlank() }
            ?.toLong()
            ?.toTaskId()
            ?: tasks.first().taskId
        call.respondHtmx {
            addHtml {
                if (isTable) {
                    tbody {
                        for (task in tasks) {
                            TaskRow(task)
                        }
                    }
                } else {
                    for (task in tasks) {
                        SimpleOption(
                            value = task.taskId.toString(),
                            text = task.name,
                            selected = current == task.taskId,
                        )
                    }
                }
            }
        }
    }

private fun Route.createTaskModal() =
    get("/create") {
        userWithRoleOrRespond(Role.Developer) ?: return@get
        call.respondHtmx {
            addHtml {
                CreateOrUpdateTask(task = null)
            }
        }
    }

private fun Route.editTaskModal() =
    get("/edit/{taskId}") {
        userWithRoleOrRespond(Role.Developer) ?: return@get
        val taskId = call.parameters
            .getOrFail(name = "taskId")
            .toLong()
            .toTaskId()
        val task = tasksDao.getTaskById(taskId = taskId)
        if (task == null) {
            call.respondHtmx {
                addCreateToastEvent("No task with ID = $taskId")
            }
            return@get
        }
        call.respondHtmx {
            addHtml {
                CreateOrUpdateTask(task = task)
            }
        }
    }

private fun Route.upsertTask() =
    put {
        userWithRoleOrRespond(Role.Developer) ?: return@put
        val request = call.receive<CreateOrUpdateTaskRequest>()
        request.validate()?.let {
            call.respondHtmx {
                addModalErrorMessage(it)
            }
            return@put
        }

        val message = if (request.taskId == null) {
            tasksDao.createTask(name = request.name, description = request.description)
            "Created new task"
        } else {
            tasksDao.updateTask(
                taskId = request.taskId!!,
                name = request.name,
                description = request.description,
            )
            "Updated existing task"
        }
        call.respondHtmx {
            addModalCloseEvent(request.modalId)
            addCreateToastEvent(message)
            addRefreshDataEvent()
        }
    }
