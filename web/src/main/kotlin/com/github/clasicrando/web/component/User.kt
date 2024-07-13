package com.github.clasicrando.web.component

import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserIdJson
import com.github.clasicrando.web.GridTier
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.Column
import com.github.clasicrando.web.element.Row
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
import kotlinx.html.p
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
        extraButtons =
            listOf(
                ExtraButton(
                    title = "Create User",
                    icon = "fa-plus",
                    apiUrl = apiV1Url("/users/create"),
                    target = ADD_MODAL_TARGET,
                    swap = HxSwap(swapType = SwapType.BeforeEnd),
                    httpMethod = HttpMethod.Get,
                ),
            ),
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
                    title = "Modify User",
                    url = apiV1Url("/users/modify"),
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
                    target = ADD_MODAL_TARGET,
                    swap = HxSwap(swapType = SwapType.BeforeEnd),
                )
                RowActionWithValue(
                    title = "Deactivate User",
                    url = apiV1Url("/users/deactivate"),
                    icon = "fa-lock",
                    httpMethod = HttpMethod.Post,
                    requestBody = userIdJson,
                    target = NO_DISPLAY_ELEMENT_TARGET,
                    confirmMessage = "Are you sure you want to deactivate ${user.fullName}?",
                )
            } else {
                RowActionWithValue(
                    title = "Activate User",
                    url = apiV1Url("/users/activate"),
                    icon = "fa-unlock",
                    httpMethod = HttpMethod.Post,
                    requestBody = userIdJson,
                    target = NO_DISPLAY_ELEMENT_TARGET,
                    confirmMessage = "Are you sure you want to activate ${user.fullName}?",
                )
            }
        }
    }
}

private const val USERNAME_ID = "username"
private const val FULL_NAME_ID = "fullName"
private const val PASSWORD_ID = "password"
private const val CONFIRM_PASSWORD_ID = "confirmPassword"

@Component
fun <T, C : TagConsumer<T>> C.CreateUserModal() {
    CreateModal(
        id = "modifyUserModal",
        title = "Modify User",
        postUrl = apiV1Url("/users"),
        target = NO_DISPLAY_ELEMENT_TARGET,
    ) {
        Row {
            Column(size = 3, gridTier = GridTier.Small) {
                label(classes = "col-form-label") {
                    htmlFor = USERNAME_ID
                    +"Username"
                }
            }
            Column(size = 9, gridTier = GridTier.Small) {
                input(classes = "form-control", type = InputType.text) {
                    id = USERNAME_ID
                    name = USERNAME_ID
                }
            }
        }
        Row {
            Column(size = 3, gridTier = GridTier.Small) {
                label(classes = "col-form-label") {
                    htmlFor = PASSWORD_ID
                    +"Password"
                }
            }
            Column(size = 9, gridTier = GridTier.Small) {
                input(classes = "form-control", type = InputType.password) {
                    id = PASSWORD_ID
                    name = PASSWORD_ID
                }
            }
        }
        Row {
            Column(size = 3, gridTier = GridTier.Small) {
                label(classes = "col-form-label") {
                    htmlFor = FULL_NAME_ID
                    +"Full Name"
                }
            }
            Column(size = 9, gridTier = GridTier.Small) {
                input(classes = "form-control", type = InputType.text) {
                    id = FULL_NAME_ID
                    name = FULL_NAME_ID
                }
            }
        }
        for (role in Role.entries) {
            div(classes = "form-check form-switch") {
                input(classes = "form-check-input", type = InputType.checkBox) {
                    this.role = "switch"
                    name = role.name
                    id = role.name
                }
                label(classes = "form-check-label") {
                    htmlFor = role.name
                    +role.name
                }
            }
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.ModifyUserModal(userToModify: User) {
    UpdateModalWithExtraValues(
        id = "modifyUserModal",
        title = "Modify User",
        patchUrl = apiV1Url("/users"),
        target = NO_DISPLAY_ELEMENT_TARGET,
        extraValues = UserIdJson(userToModify.userId),
    ) {
        Row {
            Column(size = 3, gridTier = GridTier.Small) {
                label(classes = "col-form-label") {
                    htmlFor = USERNAME_ID
                    +"Username"
                }
            }
            Column(size = 9, gridTier = GridTier.Small) {
                input(classes = "form-control", type = InputType.text) {
                    id = USERNAME_ID
                    name = USERNAME_ID
                    value = userToModify.username
                }
            }
        }
        Row {
            Column(size = 3, gridTier = GridTier.Small) {
                label(classes = "col-form-label") {
                    htmlFor = FULL_NAME_ID
                    +"Full Name"
                }
            }
            Column(size = 9, gridTier = GridTier.Small) {
                input(classes = "form-control", type = InputType.text) {
                    id = FULL_NAME_ID
                    name = FULL_NAME_ID
                    value = userToModify.fullName
                }
            }
        }
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

@Component
fun <T, C : TagConsumer<T>> C.ResetPasswordModal(userToModify: User) {
    UpdateModalWithExtraValues(
        id = "resetUserPassword",
        title = "Reset User Password",
        patchUrl = apiV1Url("/users/reset-password"),
        target = NO_DISPLAY_ELEMENT_TARGET,
        extraValues = UserIdJson(userToModify.userId),
    ) {
        Row {
            p { +"Username: ${userToModify.username}" }
        }
        Row {
            Column(size = 3, gridTier = GridTier.Small) {
                label(classes = "col-form-label") {
                    htmlFor = PASSWORD_ID
                    +"New Password"
                }
            }
            Column(size = 9, gridTier = GridTier.Small) {
                input(classes = "form-control", type = InputType.password) {
                    id = PASSWORD_ID
                    name = PASSWORD_ID
                }
            }
        }
        Row {
            Column(size = 3, gridTier = GridTier.Small) {
                label(classes = "col-form-label") {
                    htmlFor = CONFIRM_PASSWORD_ID
                    +"Confirm New Password"
                }
            }
            Column(size = 9, gridTier = GridTier.Small) {
                input(classes = "form-control", type = InputType.password) {
                    id = CONFIRM_PASSWORD_ID
                    name = CONFIRM_PASSWORD_ID
                }
            }
        }
    }
}
