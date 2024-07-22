package com.github.clasicrando.pipelines.model

@JvmInline
value class RunId(
    val value: Long,
)

fun Long.toRunId(): RunId = RunId(this)
