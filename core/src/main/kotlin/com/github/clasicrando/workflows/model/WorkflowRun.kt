package com.github.clasicrando.workflows.model

@JvmInline
value class WorkflowRunId(
    val value: Long,
) {
    override fun toString(): String = value.toString()
}

fun Long.toWorkflowRunId() = WorkflowRunId(this)

data class WorkflowRun(
    val workflowRunId: WorkflowRunId,
)
