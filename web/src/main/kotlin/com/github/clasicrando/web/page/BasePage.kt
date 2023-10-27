package com.github.clasicrando.web.page

import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.component.mainNav
import com.github.clasicrando.web.element.noscript
import io.ktor.server.html.Template
import io.ktor.utils.io.charsets.name
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.lang
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.title

abstract class BasePage(
    private val user: User? = null,
    private val stylesheetHref: String? = null,
    private val pageTitle: String? = null,
) : Template<HTML> {
    abstract fun FlowContent.innerContent()

    @Suppress("ktlint:standard:max-line-length")
    override fun HTML.apply() {
        lang = "en-US"
        head {
            meta(charset = Charsets.UTF_8.name)
            meta(name = "viewport", content = "width=device-width, initial-scale=1")
            meta(name = "theme-color", content = "#000000")
            link(rel = "icon", href = "/assets/favicon.ico")
            link(rel = "stylesheet", href = "/assets/style.css")
            link(
                rel = "stylesheet",
                href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css",
            ) {
                integrity = "sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM"
                attributes["crossorigin"] = "anonymous"
            }
            stylesheetHref?.let {
                link(rel = "stylesheet", href = it)
            }
            script(src = "/assets/htmx.min.js") {}
            script(src = "/assets/json-enc.js") {}
            script(type = "module", src = "/assets/utils.js") {}
            script(src = "/assets/fontawesome/js/solid.js") {
                defer = true
            }
            script(src = "/assets/fontawesome/js/fontawesome.js") {
                defer = true
            }
            title(content = "EnviroManager${pageTitle?.let { " - $it" } ?: ""}")
        }
        noscript(content = "Javascript must be enabled for most site features to work")
        body(classes = "p-3 m-0 border-0") {
            div(classes = "container-fluid") {
                mainNav(user = user)
                innerContent()
            }
            div(classes = "toast-container top-0 end-0 p-3") {
                id = "toasts"
            }
            div {
                id = "modals"
            }
            div(classes = "d-none") {
                id = "noDisplay"
            }
        }
    }
}
