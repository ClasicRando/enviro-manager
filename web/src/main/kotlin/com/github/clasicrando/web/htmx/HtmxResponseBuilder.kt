package com.github.clasicrando.web.htmx

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.Placeholder
import io.ktor.server.html.insert
import io.ktor.server.response.respond
import kotlinx.html.TagConsumer
import kotlinx.html.stream.appendHTML
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

typealias HtmxContentCollector = TagConsumer<StringBuilder>

class HtmxResponseBuilder {
    private val triggers: MutableMap<String, JsonElement> = mutableMapOf()
    var triggerData: JsonObject? = null
    var target: String? = null
    var swap: HxSwap? = null
    var responseContent: String = ""

    fun addCreateToastEvent(message: String) {
        triggers["createToast"] = JsonPrimitive(message)
    }

    fun addRefreshDataEvent() {
        triggers["refreshData"] = JsonNull
    }

    private fun finishTriggers() {
        triggerData = JsonObject(triggers)
    }

    fun addHtml(chunk: HtmxContentCollector.() -> Unit) {
        val placeholder = Placeholder<HtmxContentCollector>()
        finishTriggers()
        responseContent =
            buildString {
                placeholder {
                    this.chunk()
                }
                appendHTML().insert(placeholder)
            }
    }
}

suspend fun ApplicationCall.respondHtmx(block: HtmxResponseBuilder.() -> Unit) {
    val builder = HtmxResponseBuilder()
    builder.block()
    builder.triggerData?.let {
        response.headers.append("HX-Trigger", it.toString())
    }
    builder.target?.let {
        response.headers.append("HX-Retarget", it)
    }
    builder.swap?.let {
        response.headers.append("HX-Swap", it.toString())
    }
    respond(
        TextContent(
            builder.responseContent,
            ContentType.Text.Html.withCharset(Charsets.UTF_8),
            HttpStatusCode.OK,
        ),
    )
}
