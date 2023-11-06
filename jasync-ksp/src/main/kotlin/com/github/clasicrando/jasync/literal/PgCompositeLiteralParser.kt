package com.github.clasicrando.jasync.literal

import com.github.jasync.sql.db.general.ColumnData
import com.github.jasync.sql.db.postgresql.column.ColumnTypes
import com.github.jasync.sql.db.postgresql.column.PostgreSQLColumnDecoderRegistry
import com.github.jasync.sql.db.postgresql.messages.backend.PostgreSQLColumnData
import io.netty.buffer.Unpooled
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

fun <T : Any> parseComposite(
    composite: String,
    parsing: PgCompositeLiteralParser.() -> T,
): T? {
    return composite.takeIf { it.isNotBlank() }
        ?.let {
            val parser = PgCompositeLiteralParser(it)
            parser.parsing()
        }
}

@Suppress("UNCHECKED_CAST")
class PgCompositeLiteralParser internal constructor(private val literal: String) {
    private val charBuffer = literal.substring(1, literal.length - 1).toMutableList()
    private var isDone = false

    private fun columnDataFromTypeId(typeOid: UInt): ColumnData {
        return PostgreSQLColumnData(
            name = "",
            tableObjectId = 0,
            columnNumber = 0,
            dataType = typeOid.toInt(),
            dataTypeSize = 0L,
            dataTypeModifier = 0,
            fieldFormat = 0,
        )
    }

    private fun <T> decodeFromTypeId(
        typeOid: Int,
        value: String,
    ): T {
        return PostgreSQLColumnDecoderRegistry.Instance.decode(
            kind = columnDataFromTypeId(typeOid.toUInt()),
            value = Unpooled.copiedBuffer(value.toByteArray()),
            charset = Charsets.UTF_8,
        ) as T
    }

    @PublishedApi
    internal inline fun <T : Any?> tryParseNextBuffer(
        expectedType: String,
        errorInfo: () -> String? = { null },
        action: (String) -> T,
    ): T? {
        val value = readNextBuffer() ?: return null
        if (value.isEmpty() || value.lowercase() == "null") {
            return null
        }
        return runCatching {
            action(value)
        }.getOrElse {
            val error = LiteralParseError(expectedType, value, errorInfo())
            error.addSuppressed(it)
            throw error
        }
    }

    fun readNextBuffer(): String? {
        if (isDone) {
            throw ExhaustedBuffer()
        }
        var foundDelimiter = false
        var quoted = false
        var inQuotes = false
        var inEscape = false
        var previousChar = '\u0000'
        val builder = StringBuilder()
        while (charBuffer.isNotEmpty()) {
            val char = charBuffer.removeFirst()
            when {
                inEscape -> {
                    builder.append(char)
                    inEscape = false
                }
                char == '"' && inQuotes -> inQuotes = false
                char == '"' -> {
                    inQuotes = true
                    quoted = true
                    if (previousChar == '"') {
                        builder.append(char)
                    }
                }
                char == '\\' && !inEscape -> inEscape = true
                char == DELIMITER && !inQuotes -> {
                    foundDelimiter = true
                    break
                }
                else -> builder.append(char)
            }
            previousChar = char
        }
        isDone = !foundDelimiter
        return builder.takeIf { it.isNotEmpty() || quoted }?.toString()
    }

    fun readBoolean(): Boolean? {
        val buffer = readNextBuffer() ?: return null
        return when (buffer) {
            "t" -> true
            "f" -> false
            else -> throw LiteralParseError("Boolean", buffer, "value not t/f")
        }
    }

    fun readShort(): Short? =
        tryParseNextBuffer("Short") {
            it.toShort()
        }

    fun readInt(): Int? =
        tryParseNextBuffer("Int") {
            it.toInt()
        }

    fun readLong(): Long? =
        tryParseNextBuffer("Long") {
            it.toLong()
        }

    fun readFloat(): Float? =
        tryParseNextBuffer("Float") {
            it.toFloat()
        }

    fun readDouble(): Double? =
        tryParseNextBuffer("Double") {
            it.toDouble()
        }

    fun readBigDecimal(): BigDecimal? =
        tryParseNextBuffer("BigDecimal") {
            it.toBigDecimal()
        }

    fun readString(): String? = readNextBuffer()

    fun readLocalDate(): LocalDate? =
        tryParseNextBuffer("LocalDate") {
            decodeFromTypeId(ColumnTypes.Date, it)
        }

    fun readLocalTime(): LocalTime? =
        tryParseNextBuffer("LocalTime") {
            decodeFromTypeId(ColumnTypes.Time, it)
        }

    fun readLocalDateTime(): LocalDateTime? =
        tryParseNextBuffer("LocalDateTime") {
            decodeFromTypeId(ColumnTypes.Timestamp, it)
        }

    fun readOffsetDateTime(): OffsetDateTime? =
        tryParseNextBuffer("OffsetDateTime") {
            decodeFromTypeId(ColumnTypes.TimestampWithTimezone, it)
        }

    inline fun <reified T : Enum<T>> readEnum(): T? =
        tryParseNextBuffer("Enum") {
            enumValues<T>().first { label -> label.name.lowercase() == it.lowercase() }
        }

    fun <C : Any> readComposite(typeOid: Int): C? {
        return tryParseNextBuffer("Composite") {
            decodeFromTypeId(typeOid, it)
        }
    }

    fun <T : Any> readList(typeOid: Int): List<T?>? {
        return tryParseNextBuffer("List") {
            decodeFromTypeId(typeOid, it)
        }
    }

    companion object {
        private const val DELIMITER = ','
    }
}
