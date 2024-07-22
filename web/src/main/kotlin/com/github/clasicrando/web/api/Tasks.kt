package com.github.clasicrando.web.api

import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.tasksDao
import com.github.clasicrando.workflows.model.toTaskId
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.tasks() {
    route("/tasks") {
        getTasks()
    }
}

private fun Route.getTasks() =
    get {
        val tasks = tasksDao.getTasks()
        val current =
            call.parameters["current"]
                ?.takeIf { it.isNotBlank() }
                ?.toLong()
                ?.toTaskId()
                ?: tasks.first().taskId
        call.respondHtmx {
            addHtml {
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
