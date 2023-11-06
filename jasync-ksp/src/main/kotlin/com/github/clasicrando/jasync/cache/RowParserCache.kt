package com.github.clasicrando.jasync.cache

import com.github.clasicrando.jasync.ksp.RowParserProcessor
import com.github.clasicrando.jasync.rowparser.RowParser
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.measureTimedValue

@Suppress("UNCHECKED_CAST")
object RowParserCache {
    private val logger: KLogger = KotlinLogging.logger {}

    @PublishedApi
    internal val cache: HashMap<KType, RowParser<*>> by lazy {
        runCatching {
            val (cache, duration) =
                measureTimedValue {
                    val cache = HashMap<KType, RowParser<*>>()
                    cache.putAll(fetchCacheItems())
                    cache
                }
            logger.atInfo {
                message = "Cache has been initialized"
                payload = mapOf("duration" to duration)
            }
            cache
        }.onFailure {
            logger.atError {
                message = "Could not construct row parser cache"
                cause = it
            }
        }.getOrDefault(hashMapOf())
    }

    private fun fetchCacheItems(): Map<KType, RowParser<*>> {
        return this.getObjectField(
            className = RowParserProcessor.ROW_PARSERS_OBJECT_NAME_FULL,
            fieldName = "items",
        ) ?: mapOf()
    }

    inline fun <reified T : Any> getOrThrow(): RowParser<T> {
        val type = typeOf<T>()
        val parser = cache[type] ?: error("No cached RowParser for '$type'")
        return parser as RowParser<T>
    }

    fun loadCache() {
        cache
    }
}
