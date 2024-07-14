package com.github.clasicrando.web.api

import com.github.clasicrando.requests.CreateUserRequest
import com.github.clasicrando.requests.ModifyUserRequest
import com.github.clasicrando.requests.UserPasswordResetRequest
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.UserIdJson
import com.github.clasicrando.web.adminUserOrRespondMaybeHtmxError
import com.github.clasicrando.web.component.CreateUserModal
import com.github.clasicrando.web.component.ModifyUserModal
import com.github.clasicrando.web.component.ResetPasswordModal
import com.github.clasicrando.web.component.User
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.html.tbody
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.users() =
    route("/users") {
        getAllUsers()
        deactivateUser()
        activateUser()
        createUserModal()
        modifyUserModal()
        createUser()
        modifyUser()
        resetPasswordModal()
        resetPassword()
    }

fun Route.getAllUsers() =
    get {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondMaybeHtmxError(usersDao) ?: return@get

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
        call.adminUserOrRespondMaybeHtmxError(usersDao) ?: return@post

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
        call.adminUserOrRespondMaybeHtmxError(usersDao) ?: return@post

        val userToEnable = call.receive<UserIdJson>().userId
        usersDao.activateUser(userToEnable)

        call.respondHtmx {
            addCreateToastEvent("User Activated")
            addRefreshDataEvent()
        }
    }

fun Route.createUserModal() =
    get("/create") {
        adminUserOrRespondMaybeHtmxError() ?: return@get

        call.respondHtmx {
            addHtml {
                CreateUserModal()
            }
        }
    }

fun Route.modifyUserModal() =
    post("/modify") {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondMaybeHtmxError(usersDao) ?: return@post

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

fun Route.createUser() =
    post {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondMaybeHtmxError(usersDao) ?: return@post
        val data = call.receive<CreateUserRequest>()
        data.validate()?.let { issue ->
            call.respondHtmx {
                addModalErrorMessage(issue)
            }
            return@post
        }

        usersDao.createUser(
            username = data.username,
            fullName = data.fullName,
            password = data.password,
            roles = data.roles,
        )

        call.respondHtmx {
            addCreateToastEvent("User Created!")
            addModalCloseEvent(data.modalId)
            addRefreshDataEvent()
        }
    }

fun Route.modifyUser() =
    patch {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondMaybeHtmxError(usersDao) ?: return@patch
        val data = call.receive<ModifyUserRequest>()
        data.validate()?.let { issue ->
            call.respondHtmx {
                addModalErrorMessage(issue)
            }
            return@patch
        }

        usersDao.updateUser(
            userId = data.userId,
            username = data.username,
            fullName = data.fullName,
            roles = data.roles,
        )

        call.respondHtmx {
            addCreateToastEvent("User modified!")
            addModalCloseEvent(data.modalId)
            addRefreshDataEvent()
        }
    }

fun Route.resetPasswordModal() =
    post("/reset-password") {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondMaybeHtmxError(usersDao) ?: return@post

        val userIdToModify = call.receive<UserIdJson>().userId
        val userToModify =
            usersDao.getById(userIdToModify)
                ?: error("Could not find user to modify in the database")

        call.respondHtmx {
            addHtml {
                ResetPasswordModal(userToModify)
            }
        }
    }

fun Route.resetPassword() =
    patch("/reset-password") {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondMaybeHtmxError(usersDao) ?: return@patch
        val data = call.receive<UserPasswordResetRequest>()
        data.validate()?.let { issue ->
            call.respondHtmx {
                addModalErrorMessage(issue)
            }
            return@patch
        }

        usersDao.resetPassword(userId = data.userId, newPassword = data.password)

        call.respondHtmx {
            addCreateToastEvent("User password reset!")
            addModalCloseEvent(data.modalId)
        }
    }
