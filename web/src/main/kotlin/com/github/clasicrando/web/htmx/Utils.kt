package com.github.clasicrando.web.htmx

import kotlinx.html.A
import kotlinx.html.FlowContent

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
private const val HYPER_SCRIPT = "_"
private const val HX_EXT = "hx-ext"
private const val HTMX_JSON_ENCODING_EXT = "json-enc"

var A.hxBoost
    get() = attributes[HX_BOOST]?.toBoolean()
    set(value) {
        value?.let { attributes[HX_BOOST] = if (it) "true" else "false" }
    }

var FlowContent.hxOnClick
    get() = attributes[HX_ON_CLICK]
    set(value) {
        value?.let { attributes[HX_ON_CLICK] = value }
    }

var FlowContent.hxTrigger
    get() = attributes[HX_TRIGGER]
    set(value) {
        value?.let { attributes[HX_TRIGGER] = value }
    }

var FlowContent.hxGet
    get() = attributes[HX_GET]
    set(value) {
        value?.let { attributes[HX_GET] = value }
    }

var FlowContent.hxPost
    get() = attributes[HX_POST]
    set(value) {
        value?.let { attributes[HX_POST] = value }
    }

var FlowContent.hxPut
    get() = attributes[HX_PUT]
    set(value) {
        value?.let { attributes[HX_PUT] = value }
    }

var FlowContent.hxPatch
    get() = attributes[HX_PATCH]
    set(value) {
        value?.let { attributes[HX_PATCH] = value }
    }

var FlowContent.hxDelete
    get() = attributes[HX_DELETE]
    set(value) {
        value?.let { attributes[HX_DELETE] = value }
    }

var FlowContent.hxIndicator
    get() = attributes[HX_INDICATOR]
    set(value) {
        value?.let { attributes[HX_INDICATOR] = value }
    }

var FlowContent.hxTarget
    get() = attributes[HX_TARGET]
    set(value) {
        value?.let { attributes[HX_TARGET] = value }
    }

var FlowContent.hxPushUrl
    get() = attributes[HX_PUSH_URL]
    set(value) {
        value?.let { attributes[HX_PUSH_URL] = value }
    }

var FlowContent.hyperscript
    get() = attributes[HYPER_SCRIPT]
    set(value) {
        value?.let { attributes[HYPER_SCRIPT] = value }
    }

var FlowContent.htmxJsonEncoding
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
