package com.github.clasicrando.pipelines.data

import com.github.clasicrando.pipelines.model.PipelineState

interface PipelineStateDao {
    suspend fun getAll(): List<PipelineState>
}
