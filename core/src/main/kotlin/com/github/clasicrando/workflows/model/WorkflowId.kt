package com.github.clasicrando.workflows.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class WorkflowId(
    val value: Long,
) {
    override fun toString(): String = value.toString()
}
