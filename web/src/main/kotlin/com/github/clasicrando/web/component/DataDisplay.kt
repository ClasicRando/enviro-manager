package com.github.clasicrando.web.component

import com.github.clasicrando.web.MAIN_CONTENT_ID
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.htmxJsonEncoding
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
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h3
import kotlinx.html.h5
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.role
import kotlinx.html.select
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.textArea
import kotlinx.html.thead
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

private val LOCAL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm")
private val LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
private val LOCAL_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

fun FlowContent.dataEditField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: Any? = null,
    inputType: InputType = InputType.text,
) {
    val inputValue =
        when (data) {
            null -> ""
            is OffsetDateTime ->
                data
                    .atZoneSameInstant(ZoneId.systemDefault())
                    .format(LOCAL_DATETIME_FORMAT)
            is LocalDateTime -> data.format(LOCAL_DATETIME_FORMAT)
            is LocalDate -> data.format(LOCAL_DATE_FORMAT)
            is LocalTime -> data.format(LOCAL_TIME_FORMAT)
            else -> data.toString()
        }
    label(classes = "col-sm-1 col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        input(classes = "form-control", type = inputType) {
            value = inputValue
            id = fieldId
            name = fieldId
        }
    }
}

fun FlowContent.dataEditArea(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: String?,
) {
    label(classes = "col-sm-1 col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        textArea(classes = "form-control") {
            id = fieldId
            name = fieldId
            style = "height: 200px"
            +(data ?: "")
        }
    }
}

fun FlowContent.dataSelectionField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    selectionItems: List<Pair<String, String>>,
    initValue: String? = null,
    initDisplay: String? = null,
) {
    val selectedIndex =
        selectionItems.indexOfFirst {
            it.first == initValue || it.second == initDisplay
        }
    label(classes = "col-sm-1 col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        select(classes = "data-field form-control") {
            id = fieldId
            name = fieldId
            for ((i, pair) in selectionItems.withIndex()) {
                val (value, display) = pair
                option {
                    this.value = value
                    if (i == selectedIndex) {
                        selected = true
                    }
                    +display
                }
            }
        }
    }
}

fun FlowContent.dataDisplayArea(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: String?,
) {
    label(classes = "col-sm-1 col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        textArea(classes = "data-field form-control") {
            readonly = true
            id = fieldId
            style = "height: 200px"
            +(data ?: "")
        }
    }
}

fun FlowContent.dataDisplayField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: Any?,
) {
    label(classes = "col-sm-1 col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        input(classes = "data-field form-control") {
            value = data.displayValue()
            readonly = true
            id = fieldId
        }
    }
}

inline fun <T, C : TagConsumer<T>> C.dataDisplay(
    title: String,
    dataUrl: String,
    editUrl: String? = null,
    crossinline block: FlowContent.() -> Unit,
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
                        hxSwap(SwapType.InnerHtml)
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

inline fun <T, C : TagConsumer<T>> C.dataEdit(
    title: String,
    patchUrl: String,
    cancelUrl: String,
    crossinline data: FlowContent.() -> Unit,
) {
    form {
        div(classes = "btn-toolbar mt-1") {
            role = "toolbar"
            h3 { +title }
            div(classes = "btn-group ms-auto") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    attributes["title"] = "Cancel"
                    hxGet = cancelUrl
                    hxTarget = "#$MAIN_CONTENT_ID"
                    hxSwap(SwapType.InnerHtml)
                    i(classes = "fa-solid fa-xmark")
                }
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    attributes["title"] = "Confirm"
                    hxPatch = patchUrl
                    hxTarget = "#$MAIN_CONTENT_ID"
                    hxSwap(SwapType.InnerHtml)
                    htmxJsonEncoding = true
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
