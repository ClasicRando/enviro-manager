package com.github.clasicrando.web.api

import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.UserId
import com.github.clasicrando.users.model.UserIdJson
import com.github.clasicrando.web.adminUserOrRespondHtmxError
import com.github.clasicrando.web.component.ModifyUserModal
import com.github.clasicrando.web.component.User
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.html.tbody
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
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.users() =
    route("/users") {
        getAllUsers()
        deactivateUser()
        activateUser()
        modifyUserModal()
        modifyUser()
    }

fun Route.getAllUsers() =
    get {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@get

        val users = usersDao.getAll()

        call.respondHtmx {
            addHtml {
                tbody {
                    for (user in users) {
                        User(user)
                    }
                }
            }
        }
    }

fun Route.deactivateUser() =
    post("/deactivate") {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@post

        val userToDeactivate = call.receive<UserIdJson>().userId
        usersDao.deactivateUser(userToDeactivate)

        call.respondHtmx {
            addCreateToastEvent("User Deactivated")
            addRefreshDataEvent()
        }
    }

fun Route.activateUser() =
    post("/activate") {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@post

        val userToEnable = call.receive<UserIdJson>().userId
        usersDao.activateUser(userToEnable)

        call.respondHtmx {
            addCreateToastEvent("User Activated")
            addRefreshDataEvent()
        }
    }

fun Route.modifyUserModal() =
    post("/modify") {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@post

        val userIdToModify = call.receive<UserIdJson>().userId
        val userToModify =
            usersDao.getById(userIdToModify)
                ?: error("Could not find user to modify in the database")

        call.respondHtmx {
            addHtml {
                ModifyUserModal(userToModify)
            }
        }
    }

@Serializable(with = ModifyUserRolesRequest.Companion::class)
data class ModifyUserRolesRequest(
    val userId: UserId,
    val modalId: String,
    val username: String,
    val fullName: String,
    val roles: List<Role>,
) {
    companion object : KSerializer<ModifyUserRolesRequest> {
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
            value: ModifyUserRolesRequest,
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
        override fun deserialize(decoder: Decoder): ModifyUserRolesRequest =
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
                ModifyUserRolesRequest(
                    userId = userId ?: error("Missing 'userId' value in request body"),
                    modalId = modalId ?: error("Missing 'modalId' value in request body"),
                    username = username ?: error("Missing 'username' value in request body"),
                    fullName = fullName ?: error("Missing 'fullName' value in request body"),
                    roles = roles,
                )
            }
    }
}

fun Route.modifyUser() =
    put {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@put
        val data = call.receive<ModifyUserRolesRequest>()

        usersDao.updateUser(
            userId = data.userId,
            username = data.username,
            fullName = data.fullName,
            roles = data.roles,
        )

        call.respondHtmx {
            addCreateToastEvent("User Modified!")
            addModalCloseEvent(data.modalId)
            addRefreshDataEvent()
        }
    }
