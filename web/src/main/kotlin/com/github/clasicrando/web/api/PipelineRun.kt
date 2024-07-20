package com.github.clasicrando.web.api

import com.github.clasicrando.web.component.PipelineRun
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.pipelineRunsDao
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.html.tbody

fun Route.pipelineRuns() {
    route("/pipeline-runs") {
        getPipelineRuns()
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
