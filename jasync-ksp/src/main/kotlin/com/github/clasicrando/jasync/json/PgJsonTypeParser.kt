package com.github.clasicrando.jasync.json

import com.github.clasicrando.jasync.type.TypeParser
import kotlin.reflect.KClass

object PgJsonTypeParser : TypeParser<PgJson> {
    override val type: KClass<PgJson> = PgJson::class
    override val typeName: String = "jsonb"
    override val hasArrayType: Boolean = true

    override fun decodeFromString(value: String): PgJson? {
        return value.takeIf { it.isNotBlank() }?.let { PgJson(it) }
    }

    override fun PgJson.encodeToString(): String {
        return this.json.toString()
    }
}
