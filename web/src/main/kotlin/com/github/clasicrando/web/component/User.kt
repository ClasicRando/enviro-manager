package com.github.clasicrando.web.component

import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserIdJson
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.api.apiV1Url
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
                RowActionWithValue(
                    title = "Add Role",
                    url = apiV1Url("/users/roles/add"),
                    icon = "fa-plus",
                    requestBody = userIdJson,
                    httpMethod = HttpMethod.Get,
                )
                RowActionWithValue(
                    title = "Revoke Role",
                    url = apiV1Url("/users/roles/revoke"),
                    icon = "fa-minus",
                    requestBody = userIdJson,
                    httpMethod = HttpMethod.Get,
                )
                RowActionWithValue(
                    title = "Reset Password",
                    url = apiV1Url("/users/reset-password"),
                    icon = "fa-rotate-right",
                    requestBody = userIdJson,
                    httpMethod = HttpMethod.Get,
                )
                RowActionWithValue(
                    title = "Disable User",
                    url = apiV1Url("/users/disable"),
                    icon = "fa-lock",
                    httpMethod = HttpMethod.Post,
                    requestBody = userIdJson,
                    target = NO_DISPLAY_ELEMENT_TARGET,
                )
            } else {
                RowActionWithValue(
                    title = "Enable User",
                    url = apiV1Url("/users/enable"),
                    icon = "fa-unlock",
                    httpMethod = HttpMethod.Post,
                    requestBody = userIdJson,
                    target = NO_DISPLAY_ELEMENT_TARGET,
                )
            }
        }
    }
}
