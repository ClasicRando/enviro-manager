package com.github.clasicrando.web.htmx

import kotlinx.html.A
import kotlinx.html.FlowContent
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

private const val HX_BOOST = "hx-boost"
private const val HX_ON_CLICK = "hx-on:click"
private const val HX_TRIGGER = "hx-trigger"
private const val HX_GET = "hx-get"
private const val HX_POST = "hx-post"
private const val HX_PUT = "hx-put"
private const val HX_PATCH = "hx-patch"
private const val HX_DELETE = "hx-delete"
private const val HX_INDICATOR = "hx-indicator"
private const val HX_TARGET = "hx-target"
private const val HX_PUSH_URL = "hx-push-url"
private const val HX_VALS = "hx-vals"
private const val HYPER_SCRIPT = "_"
private const val HX_EXT = "hx-ext"
private const val HTMX_JSON_ENCODING_EXT = "json-enc"

var A.hxBoost: Boolean?
    get() = attributes[HX_BOOST]?.toBoolean()
    set(value) {
        value?.let { attributes[HX_BOOST] = if (it) "true" else "false" }
    }

var FlowContent.hxOnClick: String?
    get() = attributes[HX_ON_CLICK]
    set(value) {
        value?.let { attributes[HX_ON_CLICK] = value }
    }

var FlowContent.hxTrigger: String?
    get() = attributes[HX_TRIGGER]
    set(value) {
        value?.let { attributes[HX_TRIGGER] = value }
    }

var FlowContent.hxGet: String?
    get() = attributes[HX_GET]
    set(value) {
        value?.let { attributes[HX_GET] = value }
    }

var FlowContent.hxPost: String?
    get() = attributes[HX_POST]
    set(value) {
        value?.let { attributes[HX_POST] = value }
    }

var FlowContent.hxPut: String?
    get() = attributes[HX_PUT]
    set(value) {
        value?.let { attributes[HX_PUT] = value }
    }

var FlowContent.hxPatch: String?
    get() = attributes[HX_PATCH]
    set(value) {
        value?.let { attributes[HX_PATCH] = value }
    }

var FlowContent.hxDelete: String?
    get() = attributes[HX_DELETE]
    set(value) {
        value?.let { attributes[HX_DELETE] = value }
    }

var FlowContent.hxIndicator: String?
    get() = attributes[HX_INDICATOR]
    set(value) {
        value?.let { attributes[HX_INDICATOR] = value }
    }

var FlowContent.hxTarget: String?
    get() = attributes[HX_TARGET]
    set(value) {
        value?.let { attributes[HX_TARGET] = value }
    }

var FlowContent.hxPushUrl: String?
    get() = attributes[HX_PUSH_URL]
    set(value) {
        value?.let { attributes[HX_PUSH_URL] = value }
    }

fun FlowContent.hxVals(json: String) {
    attributes[HX_VALS] = json
}

inline fun <reified T> FlowContent.hxVals(json: T) {
    hxVals(json, serializer())
}

fun <T> FlowContent.hxVals(
    json: T,
    serializationStrategy: SerializationStrategy<T>,
) {
    attributes[HX_VALS] = Json.encodeToString(serializationStrategy, json)
}

var FlowContent.hyperscript: String?
    get() = attributes[HYPER_SCRIPT]
    set(value) {
        value?.let { attributes[HYPER_SCRIPT] = value }
    }

var FlowContent.htmxJsonEncoding: Boolean
    get() = attributes[HX_EXT]?.contains(HTMX_JSON_ENCODING_EXT) ?: false
    set(value) {
        val ext = attributes[HX_EXT] ?: ""
        val containsExtension = ext.contains(HTMX_JSON_ENCODING_EXT)
        when {
            value && containsExtension -> return
            value -> attributes[HX_EXT] = "$ext,json-enc".trim(',')
            !value && containsExtension -> ext.replace("json-enc", "").trim(',')
            else -> return
        }
    }

fun FlowContent.confirmAction(message: String) {
    hxTrigger = "confirmed"
    val finalMessage = message.replace("'", "\\'")
    hyperscript =
        """
        on click
            call Swal.fire({title: 'Confirm', text:'$finalMessage'})
            if result.isConfirmed trigger confirmed
        """.trimIndent()
}
