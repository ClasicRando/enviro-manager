package com.github.clasicrando.web.component

import com.github.clasicrando.web.MAIN_CONTENT_TARGET
import com.github.clasicrando.web.element.Column
import com.github.clasicrando.web.element.Row
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
import kotlinx.html.option

@Component
inline fun FlowContent.CreateForm(
    title: String,
    postUrl: String,
    cancelUrl: String,
    crossinline formContent: FORM.() -> Unit,
) {
    consumer.CreateForm(title, postUrl, cancelUrl, formContent)
}

@Component
inline fun <T, C : TagConsumer<T>> C.CreateForm(
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
        Row {
            Column(classes = "text-center") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    hxTarget = MAIN_CONTENT_TARGET
                    hxGet = cancelUrl
                    +"Cancel"
                }
            }
            Column(classes = "text-center") {
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

@Component
inline fun FlowContent.EditForm(
    title: String,
    patchUrl: String,
    cancelUrl: String,
    crossinline formContent: FORM.() -> Unit,
) {
    consumer.EditForm(title, patchUrl, cancelUrl, formContent)
}

@Component
inline fun <T, C : TagConsumer<T>> C.EditForm(
    title: String,
    patchUrl: String,
    cancelUrl: String,
    crossinline formContent: FORM.() -> Unit,
) {
    h5(classes = "text-center") {
        +title
    }
    form(classes = "modify-form mx-auto") {
        formContent()
        Row {
            Column(classes = "text-center") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    hxTarget = MAIN_CONTENT_TARGET
                    hxPatch = cancelUrl
                    +"Cancel"
                }
            }
            Column(classes = "text-center") {
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

@Component
fun <T, C : TagConsumer<T>> C.SimpleOption(
    value: String,
    text: String = value,
    selected: Boolean = false,
) {
    option {
        this.value = value
        this.selected = selected
        +text
    }
}
