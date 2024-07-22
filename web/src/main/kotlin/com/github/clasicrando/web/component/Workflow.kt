package com.github.clasicrando.web.component

import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.Column
import com.github.clasicrando.web.element.Row
import com.github.clasicrando.web.htmx.HxSwap
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxOnClick
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.hxTrigger
import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowTask
import io.ktor.http.HttpMethod
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Component
fun <T, C : TagConsumer<T>> C.WorkflowsTable() {
    DataTableRefresh(
        id = "workflows",
        title = "Workflows",
        dataSource = apiV1Url("/workflows"),
        extraButtons =
            listOf(
                ExtraButton(
                    title = "Create Workflow",
                    apiUrl = apiV1Url("/workflows/create"),
                    icon = "fa-plus",
                    target = ADD_MODAL_TARGET,
                    swap = HxSwap(swapType = SwapType.BeforeEnd),
                    httpMethod = HttpMethod.Get,
                ),
            ),
    ) {
        tr {
            th { +"Tasks" }
            th { +"ID" }
            th { +"Name" }
            th { +"Is Deprecated?" }
            th { +"New Workflow" }
            th { +"Actions" }
        }
    }
}

@Component
fun TBODY.Workflow(workflow: Workflow) {
    RowWithDetails(
        detailId = "tasks${workflow.workflowId}",
        columnCount = 6,
        detailsUrl = apiV1Url("/workflows/tasks/${workflow.workflowId}"),
        detailsHeader = {
            tr {
                th { +"Task Order" }
                th { +"Task ID" }
                th { +"Name" }
                th { +"Description" }
                th { +"Default Input Parameters" }
            }
        },
    ) {
        DataCell(workflow.workflowId.value)
        DataCell(workflow.name)
        td {
            i(classes = if (workflow.isDeprecated) "fa-check" else "fa-x")
        }
        DataCell(workflow.newWorkflow)
        td {
            RowAction(
                title = "Edit Workflow",
                url = apiV1Url("/workflows/edit/${workflow.workflowId}"),
                icon = "fa-pen-to-square",
                target = ADD_MODAL_TARGET,
                httpMethod = HttpMethod.Get,
                swap = HxSwap(swapType = SwapType.BeforeEnd),
            )
        }
    }
}

@Component
fun TBODY.WorkflowTask(workflowTask: WorkflowTask) {
    tr {
        DataCell(workflowTask.taskOrder)
        DataCell(workflowTask.taskId)
        DataCell(workflowTask.name)
        DataCell(workflowTask.description)
        DataCell(workflowTask.defaultInputParameters?.decode<JsonElement>())
    }
}

@Component
fun <T, C : TagConsumer<T>> C.CreateOrUpdateWorkflowModal(workflow: Workflow?) {
    val extraValues =
        if (workflow != null) {
            mapOf("workflowId" to JsonPrimitive(workflow.workflowId.toString()))
        } else {
            mapOf()
        }
    CreateOrUpdateModal(
        id = "createOrUpdateWorkflow",
        title = "${if (workflow == null) "Create" else "Update"} Workflow",
        putUrl = apiV1Url("/workflows"),
        modalSize = ModalSize.Large,
        extraValues = extraValues,
    ) {
        Row {
            DataEditField(
                fieldId = "name",
                label = "Name",
                data = workflow?.name,
                columnWidth = 7,
                labelColumnWidth = 5,
            )
        }
        Row {
            Column {
                h5 { +"Tasks" }
            }
            Column {
                button(classes = "btn btn-primary", type = ButtonType.button) {
                    title = "Add Task"
                    hxGet = apiV1Url("/workflows/add-task")
                    hxTrigger = "click"
                    hxTarget = "#tasks"
                    hxSwap(swapType = SwapType.BeforeEnd)
                    i(classes = "fa-solid fa-plus")
                }
            }
        }
        div {
            id = "tasks"
            if (workflow != null) {
                div {
                    hxTrigger = "load"
                    hxGet = apiV1Url("/workflows/tasks/${workflow.workflowId}?edit=true")
                    hxSwap(swapType = SwapType.OuterHtml)
                    hxTarget = "this"
                }
            }
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.WorkflowTaskRow(workflowTask: WorkflowTask?) {
    Row(classes = "border m-1 p-2") {
        Row {
            DataEditField(
                fieldId = "taskOrders",
                label = "Task Order",
                columnWidth = 1,
                labelColumnWidth = 2,
                data = workflowTask?.taskOrder,
                inputType = InputType.tel,
            )
            DataSelectionField(
                fieldId = "taskIds",
                label = "Task",
                columnWidth = 5,
                trigger = "load",
                dataUrl = apiV1Url("/tasks?table=false&current=${workflowTask?.taskId}"),
                labelColumnWidth = 2,
            )
            Column(size = 2) {
                button(classes = "btn btn-primary") {
                    hxOnClick = "removeElement(this, '.border')"
                    i(classes = "fa-solid fa-minus")
                }
            }
        }
        Row {
            DataEditArea(
                fieldId = "defaultInputParameters",
                label = "Input Data",
                columnWidth = 10,
                data = workflowTask?.defaultInputParameters?.toString(),
                labelColumnWidth = 2,
            )
        }
    }
}
