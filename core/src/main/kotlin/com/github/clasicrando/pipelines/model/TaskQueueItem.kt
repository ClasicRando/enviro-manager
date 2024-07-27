package com.github.clasicrando.pipelines.model

import com.github.clasicrando.workflows.model.TaskRule
import com.github.clasicrando.workflows.model.TaskStatus
import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAs
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import io.github.clasicrando.kdbc.postgresql.type.PgJson
import kotlinx.datetime.Instant

data class TaskQueueItem(
    val taskOrder: Int,
    val taskName: String,
    val status: TaskStatus,
    val inputParameters: PgJson?,
    val outputParameters: PgJson?,
    val output: String?,
    val rules: List<TaskRule>,
    val start: Instant?,
    val end: Instant?,
    val progress: Short,
) {
    companion object : RowParser<TaskQueueItem> {
        override fun fromRow(row: DataRow): TaskQueueItem =
            TaskQueueItem(
                taskOrder = row.getAsNonNull("task_order"),
                taskName = row.getAsNonNull("task_name"),
                status = row.getAsNonNull("status"),
                inputParameters = row.getAs("input_parameters"),
                outputParameters = row.getAs("output_parameters"),
                output = row.getAs("output"),
                rules = row.getAs("rules") ?: listOf(),
                start = row.getAs("task_start"),
                end = row.getAs("task_end"),
                progress = row.getAs("progress") ?: 0,
            )
    }
}
