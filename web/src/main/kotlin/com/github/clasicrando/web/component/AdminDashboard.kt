package com.github.clasicrando.web.component

import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.htmx.confirmAction
import com.github.clasicrando.web.htmx.hxPost
import com.github.clasicrando.web.htmx.hxTarget
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h5

@Component
fun <T, C : TagConsumer<T>> C.AdminDashboard() {
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
    UsersTable()
}
