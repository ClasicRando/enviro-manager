package com.github.clasicrando.web.htmx

import kotlinx.html.A

fun A.hxBoost(value: Boolean) {
    attributes["hx-boost"] = if (value) "true" else "false"
}
