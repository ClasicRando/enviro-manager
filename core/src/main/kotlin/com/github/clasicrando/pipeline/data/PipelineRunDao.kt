package com.github.clasicrando.pipeline.data

import com.github.clasicrando.pipeline.model.PipelineRun
import com.github.clasicrando.pipeline.model.PipelineRunMin
import com.github.clasicrando.pipeline.model.RunId

interface PipelineRunDao {
    suspend fun getRuns(): List<PipelineRunMin>

    suspend fun getRun(runId: RunId): PipelineRun?
}
