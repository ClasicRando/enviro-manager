package com.github.clasicrando.pipeline.data

import com.github.clasicrando.pipeline.model.PipelineState

interface PipelineStateDao {
    suspend fun getAll(): List<PipelineState>
}
