package com.github.clasicrando.workflows.model

import io.github.clasicrando.kdbc.core.annotations.Rename

enum class TaskStatus {
    Waiting,
    Running,
    Paused,
    Failed,

    @Rename("Rule Broken")
    RuleBroken,
    Complete,
    Canceled,
}
