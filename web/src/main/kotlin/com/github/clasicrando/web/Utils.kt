package com.github.clasicrando.web

import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTrigger
import com.github.clasicrando.web.page.BasePage
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtmlTemplate
import kotlinx.html.div
import kotlinx.html.id

const val MAIN_CONTENT_ID = "main"

suspend inline fun ApplicationCall.respondBasePage(
    contentUrl: String,
    user: User? = null,
    stylesheetHref: String? = null,
    pageTitle: String? = null,
) {
    val template = BasePage(
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
