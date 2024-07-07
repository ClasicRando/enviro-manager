package com.github.clasicrando.web.component

import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserIdJson
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.htmx.HxSwap
import com.github.clasicrando.web.htmx.SwapType
import io.ktor.http.HttpMethod
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.role
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
                    title = "Modify Roles",
                    url = apiV1Url("/users/roles/modify"),
                    icon = "fa-pen-to-square",
                    requestBody = userIdJson,
                    httpMethod = HttpMethod.Post,
                    target = ADD_MODAL_TARGET,
                    swap = HxSwap(swapType = SwapType.BeforeEnd),
                )
                RowActionWithValue(
                    title = "Reset Password",
                    url = apiV1Url("/users/reset-password"),
                    icon = "fa-rotate-right",
                    requestBody = userIdJson,
                    httpMethod = HttpMethod.Post,
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

@Component
fun <T, C : TagConsumer<T>> C.ModifyRolesModal(userToModify: User) {
    CreateModalWithExtraValues(
        id = "modifyRoleModal",
        title = "Modify Roles",
        postUrl = apiV1Url("/users/roles"),
        target = NO_DISPLAY_ELEMENT_TARGET,
        extraValues = UserIdJson(userToModify.userId),
    ) {
        for (role in Role.entries) {
            div(classes = "form-check form-switch") {
                input(classes = "form-check-input", type = InputType.checkBox) {
                    this.role = "switch"
                    name = role.name
                    id = role.name
                    if (userToModify.hasRole(role)) {
                        checked = true
                    }
                }
                label(classes = "form-check-label") {
                    htmlFor = role.name
                    +role.name
                }
            }
        }
    }
}
