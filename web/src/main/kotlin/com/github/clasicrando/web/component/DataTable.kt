package com.github.clasicrando.web.component

import com.github.clasicrando.web.MAIN_CONTENT_ID
import com.github.clasicrando.web.htmx.HtmxContentCollector
import com.github.clasicrando.web.htmx.HxSwap
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.hxDelete
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxIndicator
import com.github.clasicrando.web.htmx.hxOnClick
import com.github.clasicrando.web.htmx.hxPatch
import com.github.clasicrando.web.htmx.hxPost
import com.github.clasicrando.web.htmx.hxPushUrl
import com.github.clasicrando.web.htmx.hxPut
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.hxTrigger
import io.ktor.http.HttpMethod
import io.ktor.server.html.insert
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.THEAD
import kotlinx.html.TR
import kotlinx.html.button
import kotlinx.html.caption
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.role
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.thead
import kotlinx.html.tr

@HtmlTagMarker
fun TR.dataCell(value: Any?) {
    td {
        +value.displayValue()
    }
}

fun FlowContent.rowAction(
    title: String,
    url: String,
    icon: String,
    httpMethod: HttpMethod = HttpMethod.Post,
    target: String? = null,
    swap: HxSwap? = null,
    style: String? = null,
    pushUrl: String? = null,
) {
    button(classes = "btn btn-primary me-1") {
        when (httpMethod) {
            HttpMethod.Get -> hxGet = url
            HttpMethod.Post -> hxPost = url
            HttpMethod.Put -> hxPut = url
            HttpMethod.Patch -> hxPatch = url
            HttpMethod.Delete -> hxDelete = url
        }
        attributes["title"] = title
        hxTarget = target ?: "#$MAIN_CONTENT_ID"
        hxPushUrl = pushUrl
        hxSwap(swap)
        i(classes = "fa-solid $icon") {
            style?.let { this.style = it }
        }
    }
}

fun <T> TBODY.rowWithDetails(
    detailId: String,
    columnCount: Int,
    details: List<T>,
    detailsHeader: THEAD.() -> Unit,
    detailsRowBuilder: TBODY.(T) -> Unit,
) {
    val escapedDetailsId = detailId.replace("'", "\\'")
    tr {
        td {
            button(classes = "btn btn-primary") {
                this.hxOnClick = "toggleDisplay(document.getElementById('$escapedDetailsId'))"
                i(classes = "fa-solid fa-plus")
            }
        }
    }
    detailsTable(
        detailId = detailId,
        columnCount = columnCount,
        items = details,
        header = detailsHeader,
        rowBuilder = detailsRowBuilder,
    )
}

fun <T> TBODY.detailsTable(
    detailId: String,
    columnCount: Int,
    items: List<T>,
    header: THEAD.() -> Unit,
    rowBuilder: TBODY.(T) -> Unit,
) {
    tr(classes = "d-none") {
        id = detailId
        td {
            colSpan = columnCount.toString()
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
}

data class ExtraButton(
    val title: String,
    val apiUrl: String,
    val icon: String,
    val target: String? = null,
    val swap: HxSwap? = null,
)

inline fun <T> HtmxContentCollector.dataTable(
    caption: String,
    dataSource: String,
    search: Boolean = false,
    extraButtons: List<ExtraButton> = emptyList(),
    crossinline header: THEAD.() -> Unit,
    items: List<T>,
    crossinline rowBuilder: TBODY.(T) -> Unit,
) {
    val hasSearch = search && dataSource.isNotBlank()
    div(classes = "table-responsive-sm") {
        div(classes = "btn-toolbar mt-1") {
            role = "toolbar"
            if (hasSearch) {
                div(classes = "d-flex ms-auto") {
                    input(classes = "form-control me-2", type = InputType.search) {
                        placeholder = "Search"
                        name = "search"
                        hxTrigger = "keyup changed delay:500ms"
                        hxPost = "$dataSource/search"
                        hxIndicator = ".htmx-indicator"
                        hxTarget = "#$MAIN_CONTENT_ID"
                        attributes["aria-label"] = "Search"
                    }
                }
            }
            div(
                classes =
                    if (hasSearch) {
                        "btn-group"
                    } else {
                        "btn-group ms-auto"
                    },
            ) {
                if (dataSource.isNotBlank()) {
                    button(type = ButtonType.button, classes = "btn btn-secondary") {
                        hxGet = dataSource
                        hxTrigger = "click"
                        hxTarget = "#$MAIN_CONTENT_ID"
                        hxSwap(SwapType.InnerHtml)
                        hxIndicator = ".htmx-indicator"
                        i(classes = "fa-solid fa-refresh")
                    }
                }
                for (button in extraButtons) {
                    button(type = ButtonType.button, classes = "btn btn-secondary") {
                        hxPost = button.apiUrl
                        hxTrigger = "click"
                        button.target?.let {
                            hxTarget = it
                        }
                        button.swap?.let {
                            insert(it)
                        }
                        i(classes = "fa-solid ${button.icon}")
                    }
                }
            }
        }
        table(classes = "table table-stripped caption-top") {
            caption {
                +caption
                div(classes = "spinner-border htmx-indicator") {
                    role = "status"
                }
            }
            thead(block = header)
            tbody {
                for (item in items) {
                    rowBuilder(item)
                }
            }
        }
    }
}
