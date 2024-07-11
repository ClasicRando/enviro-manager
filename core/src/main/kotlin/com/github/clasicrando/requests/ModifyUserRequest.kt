package com.github.clasicrando.requests

import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.UserId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = ModifyUserRequest.Companion::class)
data class ModifyUserRequest(
    val userId: UserId,
    val modalId: String,
    val username: String,
    val fullName: String,
    val roles: List<Role>,
) : ApiRequest {
    override fun validate(): String? =
        when {
            username.isBlank() -> "Username cannot be blank"
            fullName.isBlank() -> "Full name cannot be blank"
            else -> null
        }

    companion object : KSerializer<ModifyUserRequest> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor(serialName = "ModifyUserRolesRequest") {
                element<UserId>(elementName = "userId")
                element<String>(elementName = "modalId")
                element<String>(elementName = "username")
                element<String>(elementName = "fullName")
                for (role in Role.entries) {
                    element<String>(elementName = role.name, isOptional = true)
                }
            }

        override fun serialize(
            encoder: Encoder,
            value: ModifyUserRequest,
        ) {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, UserId.serializer(), value.userId)
                encodeStringElement(descriptor, 1, value.modalId)
                Role.entries
                    .asSequence()
                    .mapIndexedNotNull { index, role ->
                        if (value.roles.contains(role)) {
                            index to role
                        } else {
                            null
                        }
                    }.forEach { (index, role) ->
                        encodeStringElement(descriptor, index, role.name)
                    }
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): ModifyUserRequest =
            decoder.decodeStructure(descriptor) {
                var userId: UserId? = null
                var modalId: String? = null
                var username: String? = null
                var fullName: String? = null
                val roles = mutableListOf<Role>()

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> userId = decodeSerializableElement(descriptor, 0, UserId.serializer())
                        1 -> modalId = decodeStringElement(descriptor, 1)
                        2 -> username = decodeStringElement(descriptor, 2)
                        3 -> fullName = decodeStringElement(descriptor, 3)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> {
                            val on = decodeStringElement(descriptor, index)
                            if (on != "on") {
                                continue
                            }
                            val roleName = descriptor.getElementName(index)
                            roles.add(Role.valueOf(roleName))
                        }
                    }
                }
                ModifyUserRequest(
                    userId = userId ?: error("Missing 'userId' value in request body"),
                    modalId = modalId ?: error("Missing 'modalId' value in request body"),
                    username = username ?: error("Missing 'username' value in request body"),
                    fullName = fullName ?: error("Missing 'fullName' value in request body"),
                    roles = roles,
                )
            }
    }
}
