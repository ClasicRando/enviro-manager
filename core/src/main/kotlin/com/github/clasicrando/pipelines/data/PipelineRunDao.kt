package com.github.clasicrando.pipelines.data

import com.github.clasicrando.pipelines.model.PipelineRun
import com.github.clasicrando.pipelines.model.PipelineRunMin
import com.github.clasicrando.pipelines.model.RunId

interface PipelineRunDao {
    suspend fun getRuns(): List<PipelineRunMin>

    suspend fun getRun(runId: RunId): PipelineRun?
}
