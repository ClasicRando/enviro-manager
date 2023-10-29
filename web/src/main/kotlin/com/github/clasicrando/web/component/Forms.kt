package com.github.clasicrando.web.component

import com.github.clasicrando.web.MAIN_CONTENT_TARGET
import com.github.clasicrando.web.element.column
import com.github.clasicrando.web.element.row
import com.github.clasicrando.web.htmx.htmxJsonEncoding
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxPatch
import com.github.clasicrando.web.htmx.hxPost
import com.github.clasicrando.web.htmx.hxTarget
import kotlinx.html.ButtonType
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.h5

inline fun FlowContent.createForm(
    title: String,
    postUrl: String,
    cancelUrl: String,
    crossinline formContent: FORM.() -> Unit,
) {
    consumer.createForm(title, postUrl, cancelUrl, formContent)
}

inline fun <T, C : TagConsumer<T>> C.createForm(
    title: String,
    postUrl: String,
    cancelUrl: String,
    crossinline formContent: FORM.() -> Unit,
) {
    h5(classes = "text-center") {
        +title
    }
    form(classes = "modify-form mx-auto") {
        formContent()
        row {
            column(classes = "text-center") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    hxTarget = MAIN_CONTENT_TARGET
                    hxGet = cancelUrl
                    +"Cancel"
                }
            }
            column(classes = "text-center") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    hxTarget = MAIN_CONTENT_TARGET
                    hxPost = postUrl
                    htmxJsonEncoding = true
                    +"Submit"
                }
            }
        }
    }
}

inline fun FlowContent.editForm(
    patchUrl: String,
    cancelUrl: String,
    crossinline formContent: FORM.() -> Unit,
) {
    consumer.editForm(patchUrl, cancelUrl, formContent)
}

inline fun <T, C : TagConsumer<T>> C.editForm(
    patchUrl: String,
    cancelUrl: String,
    crossinline formContent: FORM.() -> Unit,
) {
    form(classes = "modify-form mx-auto") {
        formContent()
        row {
            column(classes = "text-center") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    hxTarget = MAIN_CONTENT_TARGET
                    hxPatch = cancelUrl
                    +"Cancel"
                }
            }
            column(classes = "text-center") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    hxTarget = MAIN_CONTENT_TARGET
                    hxPatch = patchUrl
                    htmxJsonEncoding = true
                    +"Submit"
                }
            }
        }
    }
}
