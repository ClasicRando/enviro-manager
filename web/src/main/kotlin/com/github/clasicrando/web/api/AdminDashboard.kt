package com.github.clasicrando.web.api

import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.adminUserOrRespondHtmxError
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.confirmAction
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxPost
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.hxTrigger
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.id
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

const val ADMIN_DASHBOARD_USERS_TABLE_ID = "usersTable"

fun Route.adminDashboard() {
    route("/admin-dashboard") {
        mainDashboard()
    }
}

fun Route.mainDashboard() =
    get {
        val usersDao: UsersDao by closestDI().instance()
        call.adminUserOrRespondHtmxError(usersDao) ?: return@get

        call.respondHtmx {
            addHtml {
                div {
                    h5 { +"Actions" }
                    div(classes = "p-3") {
                        button(classes = "btn btn-danger") {
                            hxPost = "/admin-shutdown"
                            hxTarget = NO_DISPLAY_ELEMENT_TARGET
                            confirmAction("Are you sure you want to shutdown the server?")
                            +"Shutdown Server"
                        }
                    }
                }
                div {
                    id = ADMIN_DASHBOARD_USERS_TABLE_ID
                    hxTrigger = "load"
                    hxSwap(swapType = SwapType.InnerHtml)
                    hxGet = apiV1Url("/users")
                }
            }
        }
    }
