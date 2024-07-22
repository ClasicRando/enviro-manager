package com.github.clasicrando.web.component

import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.Row
import com.github.clasicrando.web.htmx.HxSwap
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.workflows.model.Task
import io.ktor.http.HttpMethod
import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import kotlinx.serialization.json.JsonPrimitive

@Component
fun <T, C : TagConsumer<T>> C.TasksTable() {
    DataTableRefresh(
        id = "tasks",
        title = "Tasks",
        dataSource = apiV1Url("/tasks"),
        extraButtons = listOf(
            ExtraButton(
                title = "Add New Task",
                apiUrl = apiV1Url("/tasks/create"),
                icon = "fa-plus",
                target = ADD_MODAL_TARGET,
                swap = HxSwap(swapType = SwapType.BeforeEnd),
                httpMethod = HttpMethod.Get,
            ),
        ),
    ) {
        tr {
            th { +"ID" }
            th { +"Name" }
            th { +"Description" }
            th { +"Actions" }
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.TaskRow(task: Task) {
    tr {
        DataCell(task.taskId)
        DataCell(task.name)
        DataCell(task.description)
        td {
            RowAction(
                title = "Edit Task",
                url = apiV1Url("/tasks/edit/${task.taskId}"),
                icon = "fa-pen-to-square",
                httpMethod = HttpMethod.Get,
                target = ADD_MODAL_TARGET,
                swap = HxSwap(swapType = SwapType.BeforeEnd),
            )
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.CreateOrUpdateTask(task: Task?) {
    val extraValues =
        if (task != null) {
            mapOf("taskId" to JsonPrimitive(task.taskId.toString()))
        } else {
            mapOf()
        }
    CreateOrUpdateModal(
        id = "createOrUpdateTask",
        title = "${if (task == null) "Create" else "Edit"} Task",
        putUrl = apiV1Url("/tasks"),
        extraValues = extraValues,
    ) {
        Row {
            DataEditField(
                fieldId = "name",
                label = "Name",
                columnWidth = 10,
                labelColumnWidth = 2,
                data = task?.name,
            )
        }
        Row {
            DataEditArea(
                fieldId = "description",
                label = "Description",
                columnWidth = 10,
                labelColumnWidth = 2,
                data = task?.description,
            )
        }
    }
}
