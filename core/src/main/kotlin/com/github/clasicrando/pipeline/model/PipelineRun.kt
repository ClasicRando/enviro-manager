package com.github.clasicrando.pipeline.model

import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.datasources.model.toDsId
import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import io.github.clasicrando.kdbc.postgresql.type.PgJson
import kotlinx.datetime.LocalDate
import kotlinx.uuid.UUID

data class PipelineRun(
    val runId: RunId,
    val dsId: DsId,
    val dataSourceCode: String,
    val recordDate: LocalDate,
    val collectionUser: String,
    val loadUser: String,
    val checkUser: String,
    val qaUser: String,
    val currentPipelineState: String,
    val collectionWorkflowRunId: UUID,
    val loadWorkflowRunId: UUID,
    val checkWorkflowRunId: UUID,
    val qaWorkflowRunId: UUID,
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
                runId = row.getAsNonNull<Long>("run_Id").toRunId(),
                dsId = row.getAsNonNull<Long>("ds_id").toDsId(),
                dataSourceCode = row.getAsNonNull("data_source_code"),
                recordDate = row.getAsNonNull("record_date"),
                collectionUser = row.getAsNonNull("collection_user"),
                loadUser = row.getAsNonNull("load_user"),
                checkUser = row.getAsNonNull("check_user"),
                qaUser = row.getAsNonNull("qa_user"),
                currentPipelineState = row.getAsNonNull("current_pipeline_state"),
                collectionWorkflowRunId = row.getAsNonNull("collection_workflow_id"),
                loadWorkflowRunId = row.getAsNonNull("load_workflow_id"),
                checkWorkflowRunId = row.getAsNonNull("check_workflow_id"),
                qaWorkflowRunId = row.getAsNonNull("qa_workflow_id"),
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
