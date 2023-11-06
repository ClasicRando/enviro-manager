package com.github.clasicrando.jasync.json

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class PgJson internal constructor(
    @PublishedApi internal val json: JsonElement,
) {
    constructor(json: String) : this(Json.parseToJsonElement(json))

    inline fun <reified T> decode(): T {
        return Json.decodeFromJsonElement(json)
    }
}
