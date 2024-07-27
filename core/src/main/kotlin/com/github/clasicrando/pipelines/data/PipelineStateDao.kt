package com.github.clasicrando.pipelines.data

import com.github.clasicrando.pipelines.model.PipelineState
import com.github.clasicrando.pipelines.model.RunId

interface PipelineStateDao {
    suspend fun getAll(): List<PipelineState>

    suspend fun getPipelineStates(runId: RunId): List<PipelineState>
}
