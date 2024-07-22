package com.github.clasicrando.workflows.model

data class TaskRule(
    val name: String,
    val failed: Boolean,
    val message: String,
)
