package com.github.clasicrando.workflows.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class TaskId(
    val value: Long,
) {
    override fun toString(): String = value.toString()
}

fun Long.toTaskId() = TaskId(this)

data class Task(
    val taskId: TaskId,
    val name: String,
    val description: String,
) {
    companion object : RowParser<Task> {
        override fun fromRow(row: DataRow): Task =
            Task(
                taskId = row.getAsNonNull<Long>("task_id").toTaskId(),
                name = row.getAsNonNull("name"),
                description = row.getAsNonNull("description"),
            )
    }
}
