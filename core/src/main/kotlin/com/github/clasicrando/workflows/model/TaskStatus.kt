package com.github.clasicrando.workflows.model

enum class TaskStatus {
    Waiting,
    Running,
    Paused,
    Failed,
    RuleBroken,
    Complete,
    Canceled,
}
