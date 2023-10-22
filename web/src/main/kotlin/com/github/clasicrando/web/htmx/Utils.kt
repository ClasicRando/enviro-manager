package com.github.clasicrando.web.htmx

import kotlinx.html.A
import kotlinx.html.FlowContent

private const val HX_BOOST = "hx-boost"
private const val HX_ON = "hx-on"
private const val HX_TRIGGER = "hx-trigger"
private const val HX_GET = "hx-get"
private const val HX_POST = "hx-post"
private const val HX_INDICATOR = "hx-indicator"
private const val HX_TARGET = "hx-target"

var A.hxBoost
    get() = attributes[HX_BOOST]?.toBoolean()
    set(value) {
        value?.let { attributes[HX_BOOST] = if (it) "true" else "false" }
    }

var FlowContent.hxOn
    get() = attributes[HX_ON]
    set(value) {
        value?.let { attributes[HX_ON] = value }
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
