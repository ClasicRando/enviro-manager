package com.github.clasicrando.requests

import com.github.clasicrando.users.model.Role
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = CreateUserRequest.Companion::class)
data class CreateUserRequest(
    val modalId: String,
    val username: String,
    val fullName: String,
    val password: String,
    val roles: List<Role>,
) : ApiRequest {
    override fun validate(): String? =
        when {
            username.isBlank() -> "Username cannot be blank"
            fullName.isBlank() -> "Full name cannot be blank"
            password.isBlank() -> "Password cannot be blank"
            else -> null
        }

    companion object : KSerializer<CreateUserRequest> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor(serialName = "ModifyUserRolesRequest") {
                element<String>(elementName = "modalId")
                element<String>(elementName = "username")
                element<String>(elementName = "fullName")
                element<String>(elementName = "password")
                element<List<String>>(elementName = "roles")
            }

        override fun serialize(
            encoder: Encoder,
            value: CreateUserRequest,
        ) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.modalId)
                encodeStringElement(descriptor, 1, value.username)
                encodeStringElement(descriptor, 2, value.fullName)
                encodeStringElement(descriptor, 3, value.password)
                encodeSerializableElement(
                    descriptor,
                    4,
                    ListSerializer(String.serializer()),
                    value.roles.map { it.dbValue },
                )
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): CreateUserRequest =
            decoder.decodeStructure(descriptor) {
                var modalId: String? = null
                var username: String? = null
                var fullName: String? = null
                var password: String? = null
                val roles = mutableListOf<Role>()

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> modalId = decodeStringElement(descriptor, 0)
                        1 -> username = decodeStringElement(descriptor, 1)
                        2 -> fullName = decodeStringElement(descriptor, 2)
                        3 -> password = decodeStringElement(descriptor, 3)
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
                CreateUserRequest(
                    modalId = modalId ?: error("Missing 'modalId' value in request body"),
                    username = username ?: error("Missing 'username' value in request body"),
                    fullName = fullName ?: error("Missing 'fullName' value in request body"),
                    password = password ?: error("Missing 'password' value in request body"),
                    roles = roles,
                )
            }
    }
}
