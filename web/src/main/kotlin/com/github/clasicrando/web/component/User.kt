package com.github.clasicrando.web.component

import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserIdJson
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.htmx.htmxJsonEncoding
import com.github.clasicrando.web.htmx.hxVals
import io.ktor.http.HttpMethod
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

@Component
fun <T, C : TagConsumer<T>> C.UsersTable() {
    DataTableRefresh(
        id = "usersTable",
        title = "Users",
        dataSource = apiV1Url("/users"),
        header = {
            tr {
                th { +"Username" }
                th { +"Full Name" }
                th { +"Roles" }
                th { +"Actions" }
            }
        },
    )
}

@Component
fun TBODY.User(user: User) {
    tr {
        dataCell(user.username)
        dataCell(user.fullName)
        dataCell(user.roles.joinToString())
        td {
            if (user.hasRole(Role.Admin)) {
                return@td
            }
            val userIdJson = UserIdJson(user.userId)
            if (user.enabled) {
                RowAction(
                    title = "Add Role",
                    url = "",
                    icon = "fa-plus",
                    httpMethod = HttpMethod.Get,
                )
                RowAction(
                    title = "Revoke Role",
                    url = "",
                    icon = "fa-minus",
                    httpMethod = HttpMethod.Get,
                )
                RowAction(
                    title = "Reset Password",
                    url = "",
                    icon = "fa-rotate-right",
                    httpMethod = HttpMethod.Get,
                )
                RowAction(
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
                RowAction(
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
