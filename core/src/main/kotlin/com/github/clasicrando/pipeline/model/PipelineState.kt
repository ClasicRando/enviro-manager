package com.github.clasicrando.pipeline.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import kotlinx.serialization.Serializable

@Serializable
data class PipelineState(
    val code: String,
    val name: String,
    val href: String,
    val workflowOrder: Short,
) {
    companion object : RowParser<PipelineState> {
        override fun fromRow(row: DataRow): PipelineState =
            PipelineState(
                code = row.getAsNonNull("code"),
                name = row.getAsNonNull("name"),
                href = row.getAsNonNull("href"),
                workflowOrder = row.getAsNonNull("workflow_order"),
            )
    }
}
