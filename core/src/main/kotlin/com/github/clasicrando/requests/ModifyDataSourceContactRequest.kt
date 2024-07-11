package com.github.clasicrando.requests

import com.github.clasicrando.datasources.model.ContactId
import kotlinx.serialization.Serializable

@Serializable
data class ModifyDataSourceContactRequest(
    val modalId: String,
    val contactId: ContactId? = null,
    val name: String,
    val email: String,
    val website: String,
    val type: String,
    val notes: String,
) : ApiRequest {
    override fun validate(): String? =
        when {
            name.isBlank() -> "Name must not be blank"
            else -> null
        }
}
