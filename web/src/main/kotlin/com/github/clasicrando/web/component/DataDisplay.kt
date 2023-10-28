package com.github.clasicrando.web.component

import com.github.clasicrando.web.MAIN_CONTENT_ID
import com.github.clasicrando.web.htmx.HtmxContentCollector
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxPatch
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.hxTrigger
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.THEAD
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.h5
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.role
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.thead
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime

fun <T> FlowContent.dataDisplayTable(
    caption: String,
    header: THEAD.() -> Unit,
    items: List<T>,
    rowBuilder: TBODY.(T) -> Unit,
) {
    div(classes = "table-responsive-sm") {
        h5(classes = "mt-4") { +caption }
        table(classes = "table table-stripped") {
            thead(block = header)
            tbody {
                for (item in items) {
                    rowBuilder(item)
                }
            }
        }
    }
}

inline fun FlowContent.dataGroup(
    title: String,
    topMargin: UInt = 2u,
    crossinline content: DIV.() -> Unit,
) {
    h5(classes = "mt-$topMargin") {
        +title
    }
    hr()
    div {
        content()
    }
}

fun FlowContent.dataDisplayField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: Any?,
    edit: Boolean = false,
) {
    var inputType: InputType? = null
    if (edit) {
        inputType = when (data) {
            is LocalDateTime, is OffsetDateTime -> InputType.dateTime
            is LocalDate -> InputType.date
            is LocalTime, is OffsetTime -> InputType.time
            else -> InputType.text
        }
    }
    label(classes = "col-sm-1 col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        input(classes = "data-field form-control", type = inputType) {
            value = data.displayValue()
            readonly = !edit
            id = fieldId
        }
    }
}

inline fun HtmxContentCollector.dataDisplay(
    title: String,
    dataUrl: String,
    editUrl: String? = null,
    crossinline block: FlowContent.() -> Unit
) {
    div {
        div(classes = "btn-toolbar mt-1") {
            role = "toolbar"
            h3 { +title }
            div(classes = "btn-group ms-auto") {
                if (!editUrl.isNullOrBlank()) {
                    button(classes = "btn btn-secondary", type = ButtonType.button) {
                        attributes["title"] = "Edit"
                        hxGet = editUrl
                        hxTrigger = "click"
                        hxTarget = "#$MAIN_CONTENT_ID"
                        hxSwap(SwapType.OuterHtml)
                        i(classes = "fa-solid fa-edit")
                    }
                }
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    attributes["title"] = "Refresh"
                    hxGet = dataUrl
                    hxTrigger = "click"
                    hxTarget = "#$MAIN_CONTENT_ID"
                    hxSwap(SwapType.InnerHtml)
                    i(classes = "fa-solid fa-refresh")
                }
            }
        }
        hr(classes = "border border-primary border-3 opacity-75 mt-1")
        div(classes = "my-1") {
            block()
        }
    }
}

inline fun HtmxContentCollector.dataEdit(
    editId: String,
    title: String,
    patchUrl: String,
    cancelUrl: String,
    crossinline data: FlowContent.() -> Unit,
) {
    val containerId = "editDisplay$editId"
    div {
        id = containerId
        div(classes = "btn-toolbar mt-1") {
            role = "toolbar"
            h3 { +title }
            div(classes = "btn-group ms-auto") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    attributes["title"] = "Cancel"
                    hxGet = cancelUrl
                    hxTarget = "#$containerId"
                    hxSwap(SwapType.OuterHtml)
                    i(classes = "fa-solid fa-xmark")
                }
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    attributes["title"] = "Confirm"
                    hxPatch = patchUrl
                    hxTarget = "#$containerId"
                    hxSwap(SwapType.OuterHtml)
                    i(classes = "fa-solid fa-check")
                }
            }
        }
        hr(classes = "border border-primary border-3 opacity-75 mt-1")
        div(classes = "my-1") {
            data()
        }
    }
}
