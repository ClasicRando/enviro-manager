package com.github.clasicrando.web.component

import com.github.clasicrando.pipelines.model.PipelineRun
import com.github.clasicrando.pipelines.model.PipelineRunMin
import com.github.clasicrando.pipelines.model.RunId
import com.github.clasicrando.pipelines.model.TaskQueueItem
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.Column
import com.github.clasicrando.web.element.Row
import io.ktor.http.HttpMethod
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.fieldSet
import kotlinx.html.i
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Component
fun <T, C : TagConsumer<T>> C.PipelineRunsTable() {
    DataTableRefresh(
        id = "pipelineRuns",
        title = "PipelineRuns",
        dataSource = apiV1Url("/pipeline-runs"),
    ) {
        tr {
            th {
                rowSpan = "2"
                +"Run ID"
            }
            th {
                rowSpan = "2"
                +"Data Source"
            }
            th {
                rowSpan = "2"
                +"Record Date"
            }
            th {
                rowSpan = "2"
                +"Pipeline State"
            }
            th {
                rowSpan = "2"
                +"Active?"
            }
            th {
                colSpan = "4"
                +"Users"
            }
            th {
                rowSpan = "2"
                +"Actions"
            }
        }
        tr {
            th {
                +"Collection"
            }
            th {
                +"Load"
            }
            th {
                +"Check"
            }
            th {
                +"QA"
            }
        }
    }
}

@Component
fun TBODY.PipelineRun(pipelineRun: PipelineRunMin) {
    tr {
        DataCell(pipelineRun.runId.value)
        DataCell(pipelineRun.dataSourceCode)
        DataCell(pipelineRun.recordDate)
        DataCell(pipelineRun.currentPipelineState)
        td {
            i(classes = if (pipelineRun.isActive) "fa-check" else "fa-x")
        }
        DataCell(pipelineRun.collectionUser)
        DataCell(pipelineRun.loadUser)
        DataCell(pipelineRun.checkUser)
        DataCell(pipelineRun.qaUser)
        td {
            RowAction(
                title = "Enter Pipeline Run",
                url = "/pipeline-runs/${pipelineRun.runId}",
                icon = "fa-right-to-bracket",
                httpMethod = HttpMethod.Get,
            )
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.PipelineRunView(pipelineRunId: RunId) {
    DataDisplay(
        id = "pipelineRunDisplay",
        title = "Pipeline Run",
        dataUrl = apiV1Url("/pipeline-runs/$pipelineRunId"),
    )
}

val plottingStatsJson = Json {
    prettyPrint = true
}

@Component
fun <T, C : TagConsumer<T>> C.PipelineRunDisplay(pipelineRun: PipelineRun) {
    fieldSet {
        DataGroup(title = "Run Details") {
            Row(classes = "align-items-center") {
                DataDisplayField(
                    fieldId = "runId",
                    label = "Run ID",
                    columnWidth = 1,
                    data = pipelineRun.runId,
                )
                DataDisplayField(
                    fieldId = "dataSourceCode",
                    label = "Data Source",
                    columnWidth = 1,
                    data = pipelineRun.dataSourceCode,
                )
                DataDisplayField(
                    fieldId = "recordDate",
                    label = "Record Date",
                    columnWidth = 1,
                    data = pipelineRun.recordDate,
                )
                DataDisplayField(
                    fieldId = "pipelineState",
                    label = "Current Pipeline State",
                    columnWidth = 2,
                    data = pipelineRun.currentPipelineState,
                    labelColumnWidth = 1,
                )
                DataIconField(
                    fieldId = "isActive",
                    label = "Active?",
                    columnWidth = 1,
                    icon = if (pipelineRun.isActive) "fa-check" else "fa-x",
                )
                DataDisplayField(
                    fieldId = "mergeType",
                    label = "Merge Type",
                    columnWidth = 1,
                    data = pipelineRun.mergeType,
                )
            }
            Row {
                DataDisplayField(
                    fieldId = "productionCount",
                    label = "Production Count",
                    columnWidth = 1,
                    data = pipelineRun.productionCount,
                )
                DataDisplayField(
                    fieldId = "stagingCount",
                    label = "Staging Count",
                    columnWidth = 1,
                    data = pipelineRun.stagingCount,
                )
                DataDisplayField(
                    fieldId = "matchCount",
                    label = "Match Count",
                    columnWidth = 1,
                    data = pipelineRun.matchCount,
                )
                DataDisplayField(
                    fieldId = "newCount",
                    label = "New Count",
                    columnWidth = 1,
                    data = pipelineRun.newCount,
                )
                DataDisplayArea(
                    fieldId = "plottingStats",
                    label = "Plotting Stats",
                    columnWidth = 3,
                    data = plottingStatsJson.encodeToString(pipelineRun.plottingStats.json),
                    height = 50,
                )
            }
        }
        DataGroup(title = "Users") {
            Row {
                DataDisplayField(
                    fieldId = "collectionUser",
                    label = "Collection",
                    columnWidth = 2,
                    data = pipelineRun.collectionUser,
                    labelColumnWidth = 1,
                )
                DataDisplayField(
                    fieldId = "loadUser",
                    label = "Load",
                    columnWidth = 2,
                    data = pipelineRun.loadUser,
                    labelColumnWidth = 1,
                )
                DataDisplayField(
                    fieldId = "checkUser",
                    label = "Check",
                    columnWidth = 2,
                    data = pipelineRun.checkUser,
                    labelColumnWidth = 1,
                )
                DataDisplayField(
                    fieldId = "qaUser",
                    label = "QA",
                    columnWidth = 2,
                    data = pipelineRun.qaUser,
                    labelColumnWidth = 1,
                )
            }
        }
        DataGroup(title = "Workflow Activity") {
            Row {
                Column(size = 9)
                DataSelectionField(
                    fieldId = "pipelineState",
                    label = "Pipeline State",
                    columnWidth = 2,
                    trigger = "load",
                    dataUrl = apiV1Url("/pipeline-runs/${pipelineRun.runId}/pipeline-states"),
                )
            }
            Row {
                DataTableRefresh(
                    id = "pipelineStateWorkflowRun",
                    title = "Workflow Run Tasks",
                    dataSource = apiV1Url("/pipeline-runs/${pipelineRun.runId}/task-queue"),
                    refreshTrigger = "click, " +
                        "refresh-pipeline-state from:body, " +
                        "change from:#pipelineState",
                    hxInclude = "#pipelineState",
                ) {
                    tr {
                        th { +"Order" }
                        th { +"Name" }
                        th { +"Status" }
                        th { +"Progress" }
                        th { +"Start" }
                        th { +"End" }
                    }
                }
            }
        }
    }
}

@Component
fun TBODY.TaskQueueItem(taskQueueItem: TaskQueueItem) {
    tr {
        DataCell(taskQueueItem.taskOrder)
        DataCell(taskQueueItem.taskName)
        DataCell(taskQueueItem.status)
        DataCell(taskQueueItem.progress)
        DataCell(taskQueueItem.start)
        DataCell(taskQueueItem.end)
    }
}
