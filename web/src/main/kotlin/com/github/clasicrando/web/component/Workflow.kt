package com.github.clasicrando.web.component

import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.Row
import com.github.clasicrando.web.htmx.HxSwap
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.workflows.model.Workflow
import com.github.clasicrando.workflows.model.WorkflowIdJson
import io.ktor.http.HttpMethod
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import kotlinx.serialization.json.JsonPrimitive

@Component
fun <T, C : TagConsumer<T>> C.WorkflowTables() {
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
            th { +"Name" }
            th { +"Workflow Definition Name" }
            th { +"Pipeline State" }
            th { +"Actions" }
        }
    }
}

@Component
fun TBODY.Workflow(workflow: Workflow) {
    tr {
        DataCell(workflow.name)
        DataCell(workflow.workflowDefinitionName)
        DataCell(workflow.pipelineState)
        td {
            RowActionWithValue(
                title = "Edit Workflow",
                url = apiV1Url("/workflows/edit"),
                icon = "fa-pen-to-square",
                target = ADD_MODAL_TARGET,
                swap = HxSwap(swapType = SwapType.BeforeEnd),
                requestBody = WorkflowIdJson(workflow.id),
            )
        }
    }
}

private const val NAME_ID = "name"
private const val WORKFLOW_DEFINITION_NAME_ID = "workflowDefinitionName"
private const val PIPELINE_STATE_ID = "pipelineState"
private const val PIPELINE_STATES_URL = "/pipeline-states"

@Component
fun <T, C : TagConsumer<T>> C.CreateOrUpdateWorkflowModal(workflow: Workflow?) {
    val extraValues =
        if (workflow != null) {
            mapOf("workflowId" to JsonPrimitive(workflow.id.toString()))
        } else {
            mapOf()
        }
    CreateOrUpdateModal(
        id = "createOrUpdateWorkflow",
        title = "${if (workflow == null) "Create" else "Update"} Workflow",
        putUrl = apiV1Url("/workflows"),
        extraValues = extraValues,
    ) {
        Row {
            DataEditField(
                fieldId = NAME_ID,
                label = "Name",
                data = workflow?.name,
                columnWidth = 7,
                labelColumnWidth = 5,
            )
        }
        Row {
            DataEditField(
                fieldId = WORKFLOW_DEFINITION_NAME_ID,
                label = "Workflow Def Name",
                data = workflow?.workflowDefinitionName,
                columnWidth = 7,
                labelColumnWidth = 5,
            )
        }
        Row {
            val pipelineStatesUrl =
                if (workflow == null) {
                    apiV1Url(PIPELINE_STATES_URL)
                } else {
                    apiV1Url("$PIPELINE_STATES_URL?current=${workflow.pipelineState}")
                }
            DataSelectionField(
                fieldId = PIPELINE_STATE_ID,
                label = "Pipeline State",
                columnWidth = 7,
                dataUrl = pipelineStatesUrl,
                trigger = "load",
                labelColumnWidth = 5,
            )
        }
    }
}
