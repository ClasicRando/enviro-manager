package com.github.clasicrando.requests

import com.github.clasicrando.workflows.model.TaskId
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrUpdateTaskRequest(
    override val modalId: String,
    val taskId: TaskId? = null,
    val name: String,
    val description: String,
) : ModalRequest {
    override fun validate(): String? =
        when {
            name.isBlank() -> "Name cannot be blank"
            description.isBlank() -> "Description cannot be blank"
            else -> null
        }
}
