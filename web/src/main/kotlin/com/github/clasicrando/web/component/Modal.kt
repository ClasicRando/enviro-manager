package com.github.clasicrando.web.component

import com.github.clasicrando.web.htmx.htmxJsonEncoding
import com.github.clasicrando.web.htmx.hxInclude
import com.github.clasicrando.web.htmx.hxPost
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.hxVals
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.role
import kotlinx.html.style
import kotlinx.html.tabIndex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

private const val MODAL_CONTAINER_ID = "modals"
const val ADD_MODAL_TARGET = "#$MODAL_CONTAINER_ID"
const val MODAL_ERROR_MESSAGE_ID = "modalErrorMessage"
const val CREATE_MODAL_FORM_ID = "createForm"

@Component
fun FlowContent.ModalContainer() {
    div {
        id = MODAL_CONTAINER_ID
    }
}

enum class ModalSize(
    val className: String,
) {
    Small("modal-sm"),
    Default(""),
    Large("modal-lg"),
    ExtraLarge("modal-xl"),
}

@Component
inline fun <T, C : TagConsumer<T>> C.CreateModal(
    id: String,
    title: String,
    postUrl: String,
    target: String,
    modalSize: ModalSize = ModalSize.Default,
    extraValues: Map<String, JsonElement> = mapOf(),
    crossinline form: FORM.() -> Unit,
) {
    val map = extraValues.plus("modalId" to JsonPrimitive(id))
    val values = Json.encodeToString(JsonObject(map))
    Modal(
        id = id,
        title = title,
        modalSize = modalSize,
        buttons = {
            button(classes = "btn btn-secondary", type = ButtonType.button) {
                hxPost = postUrl
                hxInclude = "#$CREATE_MODAL_FORM_ID"
                hxVals(values)
                hxTarget = target
                htmxJsonEncoding = true
                +"Confirm"
            }
        },
    ) {
        form {
            this.id = CREATE_MODAL_FORM_ID
            form()
        }
    }
}

@Component
inline fun <T, C : TagConsumer<T>, reified V : Any> C.CreateModalWithExtraValues(
    id: String,
    title: String,
    postUrl: String,
    target: String,
    modalSize: ModalSize = ModalSize.Default,
    extraValues: V,
    crossinline form: FORM.() -> Unit,
) {
    CreateModal(
        id = id,
        title = title,
        postUrl = postUrl,
        target = target,
        modalSize = modalSize,
        extraValues = Json.encodeToJsonElement(extraValues).jsonObject,
        form = form,
    )
}

@Component
inline fun FlowContent.Modal(
    id: String,
    title: String,
    exitButtonMessage: String = "Close",
    modalSize: ModalSize = ModalSize.Default,
    crossinline buttons: DIV.() -> Unit,
    crossinline body: DIV.() -> Unit,
) {
    consumer.Modal(
        id = id,
        title = title,
        exitButtonMessage = exitButtonMessage,
        modalSize = modalSize,
        buttons = buttons,
        body = body,
    )
}

@Component
inline fun <T, C : TagConsumer<T>> C.Modal(
    id: String,
    title: String,
    exitButtonMessage: String = "Close",
    modalSize: ModalSize = ModalSize.Default,
    crossinline buttons: DIV.() -> Unit,
    crossinline body: DIV.() -> Unit,
) {
    div(classes = "modal-backdrop fade show") {
        this.id = "$id-backdrop"
        style = "display:block;"
        tabIndex = "-1"
    }
    div(classes = "modal fade show") {
        this.id = id
        tabIndex = "-1"
        style = "display:block;"
        attributes["aria-modal"] = "true"
        role = "dialog"
        div(classes = "modal-dialog ${modalSize.className} modal-dialog-centered") {
            div(classes = "modal-content") {
                div(classes = "modal-header") {
                    h1(classes = "modal-title fs-5") { +title }
                    button(classes = "btn-close", type = ButtonType.button) {
                        attributes["aria-label"] = "Close"
                        onClick = "closeModal(this)"
                    }
                }
                div(classes = "modal-body", block = body)
                div(classes = "modal-footer") {
                    p(classes = "text-danger") { this.id = MODAL_ERROR_MESSAGE_ID }
                    button(classes = "btn btn-secondary", type = ButtonType.button) {
                        onClick = "closeModal(this)"
                        +exitButtonMessage
                    }
                    buttons()
                }
            }
        }
    }
}
