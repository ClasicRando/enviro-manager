package com.github.clasicrando.web.htmx

import com.github.clasicrando.web.component.MODAL_ERROR_MESSAGE_ID
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class HtmxResponseBuilder {
    private val triggers: MutableMap<String, JsonElement> = mutableMapOf()
    var triggerData: JsonObject? = null
        private set
    var target: String? = null
    var swap: HxSwap? = null
    var redirect: String? = null
    var pushUrl: String? = null
    var responseContent: String = ""

    fun addCreateToastEvent(message: String) {
        triggers["createToast"] = JsonObject(mapOf("message" to JsonPrimitive(message)))
    }

    fun addRefreshDataEvent() {
        triggers["refreshData"] = JsonNull
    }

    fun addModalCloseEvent(modalId: String) {
        triggers["closeModal"] = JsonObject(mapOf("id" to JsonPrimitive(modalId)))
    }

    fun addModalErrorMessage(message: String) {
        target = "#$MODAL_ERROR_MESSAGE_ID"
        swap = HxSwap(swapType = SwapType.InnerHtml)
        responseContent = message
    }

    private fun finishTriggers() {
        triggerData = JsonObject(triggers)
    }

    fun addLoadProxy(url: String) {
        responseContent =
            buildString {
                appendHTML(prettyPrint = false).apply {
                    div {
                        hxGet = url
                        hxTrigger = "load"
                        hxSwap(SwapType.OuterHtml)
                    }
                }
            }
    }

    inline fun addHtml(crossinline chunk: TagConsumer<*>.() -> Unit) {
        responseContent =
            createHTML()
                .apply { chunk() }
                .finalize()
    }

    fun pushCurrentUrl(request: ApplicationRequest) {
        pushUrl = request.uri
    }

    fun finishResponse() {
        finishTriggers()
    }
}

suspend fun ApplicationCall.respondHtmx(block: HtmxResponseBuilder.() -> Unit) {
    val builder = HtmxResponseBuilder()
    builder.block()
    builder.finishResponse()
    builder.triggerData?.let {
        response.headers.append("HX-Trigger", it.toString())
    }
    builder.target?.let {
        response.headers.append("HX-Retarget", it)
    }
    builder.swap?.let {
        response.headers.append("HX-Swap", it.toString())
    }
    builder.redirect?.let {
        response.headers.append("HX-Redirect", it)
    }
    builder.pushUrl?.let {
        response.headers.append("HX-Push-Url", it)
    }
    respond(
        TextContent(
            builder.responseContent,
            ContentType.Text.Html.withCharset(Charsets.UTF_8),
            HttpStatusCode.OK,
        ),
    )
}
