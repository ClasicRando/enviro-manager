package com.github.clasicrando.web.api

import com.github.clasicrando.web.htmx.confirmAction
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.respondHtmx
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.html.button

fun Route.adminDashboard() {
    route("/admin-dashboard") {
        mainDashboard()
    }
}

fun Route.mainDashboard() =
    get {
        call.respondHtmx {
            addHtml {
                button(classes = "btn btn-secondary") {
                    hxGet = "/shutdown"
                    hxTarget = "#noDisplay"
                    confirmAction("Are you sure you want to shutdown the server?")
                    +"Shutdown Server"
                }
            }
        }
    }
