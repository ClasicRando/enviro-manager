package com.github.clasicrando.pipelines.model

import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.datasources.model.toDsId
import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAs
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import io.github.clasicrando.kdbc.postgresql.type.PgJson
import kotlinx.datetime.LocalDate

data class PipelineRun(
    val runId: RunId,
    val dsId: DsId,
    val dataSourceCode: String,
    val recordDate: LocalDate,
    val collectionUser: String?,
    val loadUser: String?,
    val checkUser: String?,
    val qaUser: String?,
    val currentPipelineState: String,
    val isActive: Boolean,
    val productionCount: Int,
    val stagingCount: Int,
    val matchCount: Int,
    val newCount: Int,
    val plottingStats: PgJson,
    val mergeType: MergeType,
) {
    companion object : RowParser<PipelineRun> {
        override fun fromRow(row: DataRow): PipelineRun =
            PipelineRun(
                runId = row.getAsNonNull<Long>("run_id").toRunId(),
                dsId = row.getAsNonNull<Long>("ds_id").toDsId(),
                dataSourceCode = row.getAsNonNull("data_source_code"),
                recordDate = row.getAsNonNull("record_date"),
                collectionUser = row.getAs("collection_user"),
                loadUser = row.getAs("load_user"),
                checkUser = row.getAs("check_user"),
                qaUser = row.getAs("qa_user"),
                currentPipelineState = row.getAsNonNull("current_pipeline_state"),
                isActive = row.getAsNonNull("is_active"),
                productionCount = row.getAsNonNull("production_count"),
                stagingCount = row.getAsNonNull("staging_count"),
                matchCount = row.getAsNonNull("match_count"),
                newCount = row.getAsNonNull("new_count"),
                plottingStats = row.getAsNonNull<PgJson>("plotting_stats"),
                mergeType = row.getAsNonNull("merge_type"),
            )
    }
}
