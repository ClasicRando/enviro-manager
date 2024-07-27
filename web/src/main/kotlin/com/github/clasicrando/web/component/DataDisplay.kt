package com.github.clasicrando.web.component

import com.github.clasicrando.web.MAIN_CONTENT_TARGET
import com.github.clasicrando.web.element.Column
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.htmxJsonEncoding
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxPatch
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.hxTrigger
import io.ktor.http.HttpMethod
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.SELECT
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
import kotlinx.html.textArea
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
inline fun FlowContent.DataGroup(
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

@Component
fun FlowContent.DataEditField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: Any? = null,
    inputType: InputType = InputType.text,
    labelColumnWidth: Int = 1,
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
    label(classes = "col-sm-$labelColumnWidth col-form-label text-center") {
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

@Component
fun FlowContent.DataEditArea(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: String? = null,
    labelColumnWidth: Int = 1,
) {
    label(classes = "col-sm-$labelColumnWidth col-form-label text-center") {
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

@Component
inline fun FlowContent.DataSelectionField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    dataUrl: String,
    httpMethod: HttpMethod = HttpMethod.Get,
    trigger: String = "click",
    labelColumnWidth: Int = 1,
    crossinline attributes: SELECT.() -> Unit = {},
) {
    label(classes = "col-sm-$labelColumnWidth col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        select(classes = "text-center form-control") {
            id = fieldId
            name = fieldId
            hxTrigger = trigger
            setHxUrl(httpMethod, dataUrl)
            hxSwap(swapType = SwapType.InnerHtml)
            attributes()
        }
    }
}

@Component
fun FlowContent.DataSelectionField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    selectionItems: List<Pair<String, String>>,
    initValue: String? = null,
    initDisplay: String? = null,
    labelColumnWidth: Int = 1,
) {
    val selectedIndex =
        selectionItems.indexOfFirst {
            it.first == initValue || it.second == initDisplay
        }
    label(classes = "col-sm-$labelColumnWidth col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        select(classes = "text-center form-control") {
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

@Component
fun FlowContent.DataDisplayArea(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: String?,
    height: Int = 200,
    textStart: Boolean = true,
) {
    label(classes = "col-sm-1 col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        textArea(classes = "${if (textStart) "text-start" else "text-center"} form-control") {
            readonly = true
            id = fieldId
            style = "height: ${height}px"
            +(data ?: "")
        }
    }
}

@Component
fun FlowContent.DataIconField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    icon: String,
) {
    Column(classes = "text-center", size = columnWidth) {
        label(classes = "col-form-label me-3") {
            htmlFor = fieldId
            +label
        }
        i(classes = "fa-solid $icon")
    }
}

@Component
fun FlowContent.DataDisplayField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: Any?,
    labelColumnWidth: Int = 1,
) {
    label(classes = "col-sm-$labelColumnWidth col-form-label text-center") {
        htmlFor = fieldId
        +label
    }
    div(classes = "col-sm-$columnWidth") {
        input(classes = "text-center form-control") {
            value = data.displayValue()
            readonly = true
            id = fieldId
        }
    }
}

@Component
fun FlowContent.DataDisplay(
    id: String,
    title: String,
    dataUrl: String,
    editUrl: String? = null,
    editTarget: String? = null,
) {
    consumer.DataDisplay(
        id = id,
        title = title,
        dataUrl = dataUrl,
        editUrl = editUrl,
        editTarget = editTarget,
    )
}

@Component
fun <T, C : TagConsumer<T>> C.DataDisplay(
    id: String,
    title: String,
    dataUrl: String,
    editUrl: String? = null,
    editTarget: String? = null,
) {
    div {
        this.id = id
        div(classes = "btn-toolbar mt-1") {
            role = "toolbar"
            h3 { +title }
            div(classes = "btn-group ms-auto") {
                if (!editUrl.isNullOrBlank()) {
                    button(classes = "btn btn-secondary", type = ButtonType.button) {
                        attributes["title"] = "Edit"
                        hxGet = editUrl
                        hxTrigger = "click"
                        hxTarget = if (editTarget.isNullOrBlank()) "#$id" else editTarget
                        hxSwap(SwapType.OuterHtml)
                        i(classes = "fa-solid fa-edit")
                    }
                }
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    attributes["title"] = "Refresh"
                    hxGet = dataUrl
                    hxTrigger = "load, click"
                    hxTarget = "#$id div.display-block"
                    hxSwap(SwapType.InnerHtml)
                    i(classes = "fa-solid fa-refresh")
                }
            }
        }
        hr(classes = "border border-primary border-3 opacity-75 mt-1")
        div(classes = "my-1 display-block")
    }
}

@Component
inline fun <T, C : TagConsumer<T>> C.DataEdit(
    title: String,
    url: String,
    crossinline data: FlowContent.() -> Unit,
) {
    DataEdit(
        title = title,
        patchUrl = url,
        cancelUrl = url,
        data = data,
    )
}

@Component
inline fun <T, C : TagConsumer<T>> C.DataEdit(
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
                    hxTarget = MAIN_CONTENT_TARGET
                    hxSwap(SwapType.InnerHtml)
                    i(classes = "fa-solid fa-xmark")
                }
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    attributes["title"] = "Confirm"
                    hxPatch = patchUrl
                    hxTarget = MAIN_CONTENT_TARGET
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
