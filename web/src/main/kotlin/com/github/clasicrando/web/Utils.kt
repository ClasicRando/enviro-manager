package com.github.clasicrando.web

import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTrigger
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.page.BasePage
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.html.div
import kotlinx.html.id

const val MAIN_CONTENT_ID = "main"
const val MAIN_CONTENT_TARGET = "#$MAIN_CONTENT_ID"
const val NO_DISPLAY_ELEMENT_TARGET = "#noDisplay"

suspend inline fun ApplicationCall.respondBasePage(
    contentUrl: String,
    user: User? = null,
    stylesheetHref: String? = null,
    pageTitle: String? = null,
) {
    val template =
        BasePage(
            user = user,
            stylesheetHref = stylesheetHref,
            pageTitle = pageTitle,
        )
    respondHtmlTemplate(template) {
        innerContent {
            div {
                id = MAIN_CONTENT_ID
                hxGet = contentUrl
                hxTrigger = "load"
                hxSwap(SwapType.InnerHtml)
            }
        }
    }
}

suspend fun ApplicationCall.userSessionOrRedirect(): UserSession? {
    val userSession = sessions.get<UserSession>()
    if (userSession == null) {
        respondRedirect("/login")
        return null
    }
    return userSession
}

suspend fun ApplicationCall.userOrRedirect(dao: UsersDao): User? {
    val userSession = userSessionOrRedirect() ?: return null
    val user = dao.getById(userId = userSession.userId)
    if (user == null) {
        respondRedirect("/login")
        return null
    }
    return user
}

private const val NON_ADMIN_ERROR_MESSAGE =
    "Error: Cannot perform admin action since you are not an admin"

suspend fun ApplicationCall.adminUserOrRespondHtmxError(dao: UsersDao): User? {
    val userSession = sessions.get<UserSession>()
    if (userSession == null) {
        respondHtmx {
            addCreateToastEvent(NON_ADMIN_ERROR_MESSAGE)
        }
        return null
    }
    val user = dao.getById(userId = userSession.userId)
    if (user == null) {
        respondHtmx {
            addCreateToastEvent(NON_ADMIN_ERROR_MESSAGE)
        }
        return null
    }
    return user
}

val ApplicationRequest.isHtmx: Boolean get() = this.headers.contains("HX-Request")

val ApplicationRequest.isBoost: Boolean get() = this.headers.contains("HX-Boosted")
