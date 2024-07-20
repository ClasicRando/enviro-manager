package com.github.clasicrando.pipeline.model

@JvmInline
value class RunId(
    val value: Long,
)

fun Long.toRunId(): RunId = RunId(this)
