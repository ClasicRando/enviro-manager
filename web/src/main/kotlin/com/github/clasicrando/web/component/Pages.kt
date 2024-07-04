package com.github.clasicrando.web.component

import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.MAIN_CONTENT_ID
import com.github.clasicrando.web.element.noscript
import io.ktor.utils.io.charsets.name
import kotlinx.html.HTML
import kotlinx.html.TagConsumer
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.lang
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.title

@Component
inline fun HTML.BasePage(
    user: User? = null,
    stylesheetHref: String? = null,
    pageTitle: String? = null,
    crossinline innerContent: TagConsumer<*>.() -> Unit,
) {
    lang = "en-US"
    head {
        meta(charset = Charsets.UTF_8.name)
        meta(name = "viewport", content = "width=device-width, initial-scale=1")
        meta(name = "theme-color", content = "#000000")
        link(rel = "icon", href = "/assets/favicon.ico")
        link(rel = "stylesheet", href = "/assets/style.css")
        link(rel = "stylesheet", href = "/assets/bootstrap.min.css")
        link(rel = "stylesheet", href = "/assets/sweetalert2-theme-dark.css")
        stylesheetHref?.let {
            link(rel = "stylesheet", href = it)
        }
        script(src = "/assets/htmx.min.js") {}
        script(src = "/assets/json-enc.js") {}
        script(src = "/assets/hyperscript.min.js") {}
        script(src = "/assets/sweetalert2.min.js") {}
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
            MainNav(user = user)
            div {
                id = MAIN_CONTENT_ID
                innerContent(consumer)
            }
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
