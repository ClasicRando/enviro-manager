package com.github.clasicrando.pipeline.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAs
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import kotlinx.datetime.LocalDate

data class PipelineRunMin(
    val runId: RunId,
    val dataSourceCode: String,
    val recordDate: LocalDate,
    val collectionUser: String?,
    val loadUser: String?,
    val checkUser: String?,
    val qaUser: String?,
    val currentPipelineState: String,
    val isActive: Boolean,
) {
    companion object : RowParser<PipelineRunMin> {
        override fun fromRow(row: DataRow): PipelineRunMin =
            PipelineRunMin(
                runId = row.getAsNonNull<Long>("run_id").toRunId(),
                dataSourceCode = row.getAsNonNull("data_source_code"),
                recordDate = row.getAsNonNull("record_date"),
                collectionUser = row.getAs("collection_user"),
                loadUser = row.getAs("load_user"),
                checkUser = row.getAs("check_user"),
                qaUser = row.getAs("qa_user"),
                currentPipelineState = row.getAsNonNull("current_pipeline_state"),
                isActive = row.getAsNonNull("is_active"),
            )
    }
}
