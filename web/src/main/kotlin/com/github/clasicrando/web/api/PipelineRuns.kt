package com.github.clasicrando.web.api

import com.github.clasicrando.pipelines.model.toRunId
import com.github.clasicrando.web.component.PipelineRun
import com.github.clasicrando.web.component.PipelineRunDisplay
import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.component.TaskQueueItem
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.pipelineRunsDao
import com.github.clasicrando.web.pipelineStatesDao
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.tbody

fun Route.pipelineRuns() {
    route("/pipeline-runs") {
        getPipelineRuns()
        route("/{pipelineRunId}") {
            getPipelineRun()
            getPipelineRunStates()
            getTaskQueue()
        }
    }
}

private fun Route.getPipelineRuns() =
    get {
        val pipelineRuns = pipelineRunsDao.getRuns()
        call.respondHtmx {
            addHtml {
                tbody {
                    for (pipelineRun in pipelineRuns) {
                        PipelineRun(pipelineRun)
                    }
                }
            }
        }
    }

private fun Route.getPipelineRun() =
    get {
        val pipelineRunId = call.parameters.getOrFail<Long>("pipelineRunId").toRunId()
        val pipelineRun = pipelineRunsDao.getRun(pipelineRunId)
        if (pipelineRun == null) {
            call.respondHtmx {
                addCreateToastEvent("Cannot find a pipeline run for ID = $pipelineRunId")
            }
            return@get
        }

        call.respondHtmx {
            addHtml {
                PipelineRunDisplay(pipelineRun)
            }
        }
    }

private fun Route.getPipelineRunStates() =
    get("/pipeline-states") {
        val pipelineRunId = call.parameters.getOrFail<Long>("pipelineRunId").toRunId()
        val pipelineStates = pipelineStatesDao.getPipelineStates(pipelineRunId)

        call.respondHtmx {
            addTrigger("refresh-pipeline-state")
            addHtml {
                for ((i, pipelineState) in pipelineStates.withIndex()) {
                    SimpleOption(
                        value = pipelineState.code,
                        text = pipelineState.name,
                        selected = i == pipelineStates.size - 1,
                    )
                }
            }
        }
    }

private fun Route.getTaskQueue() =
    get("/task-queue") {
        val pipelineRunId = call.parameters.getOrFail<Long>("pipelineRunId").toRunId()
        val pipelineState = call.parameters.getOrFail("pipelineState")
        val workflowRunData = pipelineRunsDao.getWorkflowRunData(pipelineRunId, pipelineState)

        call.respondHtmx {
            addHtml {
                tbody {
                    for (taskQueueItem in workflowRunData) {
                        TaskQueueItem(taskQueueItem)
                    }
                }
            }
        }
    }
