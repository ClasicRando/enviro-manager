package com.github.clasicrando.web.component

import com.github.clasicrando.pipeline.model.PipelineRunMin
import com.github.clasicrando.web.api.apiV1Url
import io.ktor.http.HttpMethod
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.i
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

@Component
fun <T, C : TagConsumer<T>> C.PipelineRuns() {
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
fun <T, C : TagConsumer<T>> C.PipelineRunDisplay() {
}
