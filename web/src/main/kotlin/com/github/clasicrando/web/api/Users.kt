package com.github.clasicrando.web.api

import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.UserIdJson
import com.github.clasicrando.web.adminUserOrRespondHtmxError
import com.github.clasicrando.web.component.usersTable
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.users() =
    route("/users") {
        getAllUsers()
        disableUser()
        enableUser()
    }

fun Route.getAllUsers() =
    get {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@get

        val users = usersDao.getAll()

        call.respondHtmx {
            addHtml {
                usersTable(requestUrl = call.request.uri, users = users)
            }
        }
    }

fun Route.disableUser() =
    post("/disable") {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@post

        val userToDisable = call.receive<UserIdJson>().userId
        usersDao.disableUser(userToDisable)

        call.respondHtmx {
            addCreateToastEvent("User Disabled")
        }
    }

fun Route.enableUser() =
    post("/enable") {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@post

        val userToEnable = call.receive<UserIdJson>().userId
        usersDao.enableUser(userToEnable)

        call.respondHtmx {
            addCreateToastEvent("User Enabled")
        }
    }
