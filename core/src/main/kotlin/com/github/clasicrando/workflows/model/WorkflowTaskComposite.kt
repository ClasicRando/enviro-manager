package com.github.clasicrando.workflows.model

import io.github.clasicrando.kdbc.postgresql.type.PgJson
import kotlinx.serialization.json.JsonElement

data class WorkflowTaskComposite(
    val workflowId: Int,
    val taskOrder: Int,
    val taskId: Long,
    val defaultInputParameters: PgJson?,
) {
    constructor(
        workflowId: Int,
        taskOrder: Int,
        taskId: Long,
        defaultInputParameters: JsonElement?,
    ) :
        this(workflowId, taskOrder, taskId, defaultInputParameters?.let { PgJson(it) })
}
