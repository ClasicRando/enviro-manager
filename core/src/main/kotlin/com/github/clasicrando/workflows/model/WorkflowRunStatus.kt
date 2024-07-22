package com.github.clasicrando.workflows.model

enum class WorkflowRunStatus {
    Waiting,
    Scheduled,
    Running,
    Paused,
    Failed,
    Complete,
    Canceled,
}
