package com.github.clasicrando.pipelines.model

@JvmInline
value class RunId(
    val value: Long,
) {
    override fun toString(): String = value.toString()
}

fun Long.toRunId(): RunId = RunId(this)
