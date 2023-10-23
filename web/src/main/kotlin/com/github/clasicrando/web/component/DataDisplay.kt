package com.github.clasicrando.web.component

import com.github.clasicrando.web.htmx.HtmxContentCollector
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.hxTrigger
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.TBODY
import kotlinx.html.THEAD
import kotlinx.html.button
import kotlinx.html.caption
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.role
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.thead

fun <T> HtmxContentCollector.dataDisplayTable(
    caption: String,
    header: THEAD.() -> Unit,
    items: List<T>,
    rowBuilder: TBODY.(T) -> Unit,
) {
    div(classes = "table-responsive-sm") {
        table(classes = "table table-stripped caption-top") {
            caption { +caption }
            thead(block = header)
            tbody {
                for (item in items) {
                    rowBuilder(item)
                }
            }
        }
    }
}

fun DIV.dataField(
    fieldId: String,
    label: String,
    columnWidth: Int,
    data: Any,
) {
    label(classes = "col-sm-1 col-form-label") {
        +label
        htmlFor = fieldId
    }
    div(classes = "col-sm-$columnWidth") {
        input(classes = "data-field form-control") {
            value = data.displayValue()
            id = fieldId
        }
    }
}

fun FlowContent.dataDisplay(
    displayId: String,
    title: String,
    dataUrl: String,
) {
    div {
        div(classes = "btn-toolbar mt-1") {
            role = "toolbar"
            h3 { +title }
            div(classes = "btn-group ms-auto") {
                button(classes = "btn btn-secondary", type = ButtonType.button) {
                    attributes["title"] = "Refresh"
                    hxGet = dataUrl
                    hxTrigger = "click, load"
                    hxTarget = "#$displayId"
                    hxSwap(SwapType.InnerHtml)
                    i(classes = "fa-solid fa-refresh")
                }
            }
        }
        hr(classes = "border border-primary border-3 opacity-75 mt-1")
        div(classes = "my-1") {
            id = displayId
        }
    }
}
