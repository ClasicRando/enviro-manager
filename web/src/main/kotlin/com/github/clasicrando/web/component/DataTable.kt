package com.github.clasicrando.web.component

import com.github.clasicrando.web.MAIN_CONTENT_TARGET
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.htmx.HxSwap
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.confirmAction
import com.github.clasicrando.web.htmx.htmxJsonEncoding
import com.github.clasicrando.web.htmx.hxDelete
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxInclude
import com.github.clasicrando.web.htmx.hxIndicator
import com.github.clasicrando.web.htmx.hxOnClick
import com.github.clasicrando.web.htmx.hxPatch
import com.github.clasicrando.web.htmx.hxPost
import com.github.clasicrando.web.htmx.hxPushUrl
import com.github.clasicrando.web.htmx.hxPut
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTarget
import com.github.clasicrando.web.htmx.hxTrigger
import com.github.clasicrando.web.htmx.hxVals
import io.ktor.http.HttpMethod
import kotlinx.html.BUTTON
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.THEAD
import kotlinx.html.TR
import kotlinx.html.TagConsumer
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
import kotlinx.html.title
import kotlinx.html.tr

@Component
fun TR.DataCell(value: Any?) {
    td {
        +value.displayValue()
    }
}

@Component
inline fun FlowContent.RowAction(
    title: String,
    url: String,
    icon: String,
    httpMethod: HttpMethod = HttpMethod.Post,
    target: String? = null,
    swap: HxSwap? = null,
    style: String? = null,
    pushUrl: String? = null,
    confirmMessage: String? = null,
    crossinline block: BUTTON.() -> Unit = {},
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
        hxTarget = target ?: MAIN_CONTENT_TARGET
        hxPushUrl = pushUrl
        hxSwap(swap)
        confirmMessage?.let {
            confirmAction(it)
        }
        block()
        i(classes = "fa-solid $icon") {
            style?.let { this.style = it }
        }
    }
}

@Component
inline fun <reified T : Any> FlowContent.RowActionWithValue(
    title: String,
    url: String,
    icon: String,
    requestBody: T,
    httpMethod: HttpMethod = HttpMethod.Post,
    target: String? = null,
    swap: HxSwap? = null,
    style: String? = null,
    pushUrl: String? = null,
    confirmMessage: String? = null,
) {
    RowAction(
        title = title,
        url = url,
        icon = icon,
        httpMethod = httpMethod,
        target = target,
        swap = swap,
        style = style,
        pushUrl = pushUrl,
        confirmMessage = confirmMessage,
    ) {
        htmxJsonEncoding = true
        hxVals(requestBody)
    }
}

@Component
inline fun TBODY.RowWithDetails(
    detailId: String,
    columnCount: Int,
    detailsUrl: String,
    detailsHttpMethod: HttpMethod = HttpMethod.Get,
    crossinline detailsHeader: THEAD.() -> Unit,
    crossinline rowContents: TR.() -> Unit,
) {
    val escapedDetailsId = detailId.replace("'", "\\'")
    tr {
        td {
            button(classes = "btn btn-primary") {
                this.hxOnClick = "toggleDisplay(document.getElementById('$escapedDetailsId'))"
                setHxUrl(detailsHttpMethod, detailsUrl)
                hxTrigger = "load"
                hxTarget = "#$detailId table tbody"
                hxIndicator = ".htmx-indicator"
                hxSwap(swapType = SwapType.OuterHtml)
                i(classes = "fa-solid fa-plus")
            }
        }
        rowContents()
    }
    DetailsTable(
        detailId = detailId,
        columnCount = columnCount,
        header = detailsHeader,
    )
}

@Component
inline fun TBODY.DetailsTable(
    detailId: String,
    columnCount: Int,
    crossinline header: THEAD.() -> Unit,
) {
    tr(classes = "d-none") {
        id = detailId
        td {
            colSpan = columnCount.toString()
            table(classes = "table table-stripped") {
                caption {
                    div(classes = "spinner-border htmx-indicator") {
                        role = "status"
                    }
                }
                thead(block = header)
                tbody()
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
    val httpMethod: HttpMethod = HttpMethod.Post,
)

@Component
inline fun <T, C : TagConsumer<T>> C.DataTableRefresh(
    id: String,
    title: String,
    dataSource: String,
    search: Boolean = false,
    extraButtons: List<ExtraButton> = emptyList(),
    extraContainerClasses: String? = null,
    refreshTrigger: String = "load, click",
    hxInclude: String? = null,
    crossinline header: THEAD.() -> Unit,
) {
    val bodyTarget = "#$id tbody"
    val containerClasses =
        if (extraContainerClasses.isNullOrBlank()) {
            "table-responsive-sm"
        } else {
            "table-responsive-sm ${extraContainerClasses.trim()}"
        }
    div(classes = containerClasses) {
        div(classes = "btn-toolbar mt-1") {
            role = "toolbar"
            if (search) {
                div(classes = "d-flex ms-auto") {
                    input(classes = "form-control me-2", type = InputType.search) {
                        placeholder = "Search"
                        name = "search"
                        hxTrigger = "keyup changed delay:500ms, search"
                        hxPost = "$dataSource/search"
                        hxIndicator = ".htmx-indicator"
                        hxTarget = bodyTarget
                        attributes["aria-label"] = "Search"
                    }
                }
            }
            div(
                classes =
                    if (search) {
                        "btn-group"
                    } else {
                        "btn-group ms-auto"
                    },
            ) {
                button(type = ButtonType.button, classes = "btn btn-secondary") {
                    this.title = "Refresh"
                    hxGet = dataSource
                    hxTrigger = refreshTrigger
                    hxTarget = bodyTarget
                    if (!hxInclude.isNullOrBlank()) {
                        this.hxInclude = hxInclude
                    }
                    hxSwap(SwapType.OuterHtml)
                    hxIndicator = ".htmx-indicator"
                    i(classes = "fa-solid fa-refresh")
                }
                for (button in extraButtons) {
                    button(type = ButtonType.button, classes = "btn btn-secondary") {
                        setHxUrl(button.httpMethod, button.apiUrl)
                        hxTrigger = "click"
                        hxTarget = button.target ?: NO_DISPLAY_ELEMENT_TARGET
                        hxSwap(button.swap ?: HxSwap(swapType = SwapType.InnerHtml))
                        i(classes = "fa-solid ${button.icon}")
                    }
                }
            }
        }
        table(classes = "table table-stripped caption-top") {
            this.id = id
            caption {
                +title
                div(classes = "spinner-border htmx-indicator") {
                    role = "status"
                }
            }
            thead(block = header)
            tbody()
        }
    }
}
