package com.github.clasicrando.web.component

import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserIdJson
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.api.ADMIN_DASHBOARD_USERS_TABLE_ID
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.htmx.htmxJsonEncoding
import com.github.clasicrando.web.htmx.hxVals
import io.ktor.http.HttpMethod
import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

fun <T, C : TagConsumer<T>> C.usersTable(
    requestUrl: String,
    users: List<User>,
) {
    dataTable(
        title = "Users",
        dataSource = requestUrl,
        swapTarget = "#$ADMIN_DASHBOARD_USERS_TABLE_ID",
        header = {
            tr {
                th { +"Username" }
                th { +"Full Name" }
                th { +"Roles" }
                th { +"Actions" }
            }
        },
        items = users,
    ) {
        tr {
            dataCell(it.username)
            dataCell(it.fullName)
            dataCell(it.roles.joinToString())
            td {
                if (it.hasRole(Role.Admin)) {
                    return@td
                }
                rowAction(
                    title = "Add Role",
                    url = "",
                    icon = "fa-plus",
                    httpMethod = HttpMethod.Get,
                )
                rowAction(
                    title = "Revoke Role",
                    url = "",
                    icon = "fa-minus",
                    httpMethod = HttpMethod.Get,
                )
                rowAction(
                    title = "Reset Password",
                    url = "",
                    icon = "fa-rotate-right",
                    httpMethod = HttpMethod.Get,
                )
                val userIdJson = UserIdJson(it.userId)
                if (it.enabled) {
                    rowAction(
                        title = "Disable User",
                        url = apiV1Url("/users/disable"),
                        icon = "fa-lock",
                        httpMethod = HttpMethod.Post,
                        target = NO_DISPLAY_ELEMENT_TARGET,
                    ) {
                        htmxJsonEncoding = true
                        hxVals(userIdJson)
                    }
                } else {
                    rowAction(
                        title = "Enable User",
                        url = apiV1Url("/users/enable"),
                        icon = "fa-unlock",
                        httpMethod = HttpMethod.Post,
                        target = NO_DISPLAY_ELEMENT_TARGET,
                    ) {
                        htmxJsonEncoding = true
                        hxVals(userIdJson)
                    }
                }
            }
        }
    }
}
