package com.github.clasicrando.jasync.literal

import com.github.jasync.sql.db.postgresql.column.PostgreSQLColumnEncoderRegistry
import java.math.BigDecimal
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime

inline fun compositeLiteralBuilder(block: PgCompositeLiteralBuilder.() -> Unit): String {
    return PgCompositeLiteralBuilder().apply(block).toString()
}

class PgCompositeLiteralBuilder {
    @PublishedApi
    internal val stringBuilder =
        StringBuilder().apply {
            append('(')
        }

    /** Returns a new string with the required control characters for composites escaped */
    @PublishedApi
    internal fun replaceInComposite(value: String?): String? {
        return value?.replace("\\", "\\\\")?.replace("\"", "\"\"")
    }

    @PublishedApi
    internal fun prependCommaIfNeeded() {
        if (stringBuilder.length == 1) {
            return
        }
        stringBuilder.append(',')
    }

    inline fun <reified C : Any> appendComposite(composite: C?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        composite?.let { comp ->
            val obj = PostgreSQLColumnEncoderRegistry.Instance.encode(comp)
            stringBuilder.apply {
                append('"')
                append(replaceInComposite(obj))
                append('"')
            }
        }
        return this
    }

    private fun <T> appendIterable(iterable: Iterable<T>?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        iterable?.let { iter ->
            val arrayString = PostgreSQLColumnEncoderRegistry.Instance.encode(iter)
            stringBuilder.apply {
                append('"')
                append(replaceInComposite(arrayString))
                append('"')
            }
        }
        return this
    }

    fun <T> appendList(list: List<T>?): PgCompositeLiteralBuilder {
        return appendIterable(list)
    }

    fun <T> appendArray(array: Array<T>?): PgCompositeLiteralBuilder {
        return appendIterable(array?.asIterable())
    }

    fun appendBoolean(bool: Boolean?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        bool?.let { stringBuilder.append(if (it) 't' else 'f') }
        return this
    }

    fun appendByte(byte: Byte?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        stringBuilder.append(byte)
        return this
    }

    fun appendShort(short: Short?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        stringBuilder.append(short)
        return this
    }

    fun appendInt(int: Int?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        stringBuilder.append(int)
        return this
    }

    fun appendLong(long: Long?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        stringBuilder.append(long)
        return this
    }

    fun appendFloat(float: Float?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        stringBuilder.append(float)
        return this
    }

    fun appendDouble(double: Double?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        stringBuilder.append(double)
        return this
    }

    fun appendString(string: String?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        if (string == null) {
            return this
        }
        stringBuilder.apply {
            append('"')
            append(string.replace("\"", "\"\""))
            append('"')
        }
        return this
    }

    fun appendBigDecimal(bigDecimal: BigDecimal?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        stringBuilder.append(bigDecimal)
        return this
    }

    fun appendDate(date: Date?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        date?.let {
            stringBuilder.append(PostgreSQLColumnEncoderRegistry.Instance.encode(it))
        }
        return this
    }

    fun appendLocalDate(localDate: LocalDate?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        localDate?.let {
            stringBuilder.append(PostgreSQLColumnEncoderRegistry.Instance.encode(it))
        }
        return this
    }

    fun appendLocalDateTime(localDateTime: LocalDateTime?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        localDateTime?.let {
            stringBuilder.apply {
                append('"')
                append(PostgreSQLColumnEncoderRegistry.Instance.encode(it))
                append('"')
            }
        }
        return this
    }

    fun appendTimestamp(timestamp: Timestamp?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        timestamp?.let {
            stringBuilder.apply {
                append('"')
                append(PostgreSQLColumnEncoderRegistry.Instance.encode(it))
                append('"')
            }
        }
        return this
    }

    fun appendOffsetDateTime(offsetDateTime: OffsetDateTime?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        offsetDateTime?.let {
            stringBuilder.apply {
                append('"')
                append(PostgreSQLColumnEncoderRegistry.Instance.encode(it))
                append('"')
            }
        }
        return this
    }

    fun appendTime(time: Time?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        time?.let {
            stringBuilder.append(PostgreSQLColumnEncoderRegistry.Instance.encode(it))
        }
        return this
    }

    fun appendLocalTime(localTime: LocalTime?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        localTime?.let {
            stringBuilder.append(PostgreSQLColumnEncoderRegistry.Instance.encode(it))
        }
        return this
    }

    fun appendOffsetTime(offsetTime: OffsetTime?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        offsetTime?.let {
            stringBuilder.append(PostgreSQLColumnEncoderRegistry.Instance.encode(it))
        }
        return this
    }

    fun <E : Enum<E>> appendEnum(value: E?): PgCompositeLiteralBuilder {
        prependCommaIfNeeded()
        value?.let { stringBuilder.append(it.name) }
        return this
    }

    override fun toString(): String {
        return stringBuilder.run {
            append(')')
            toString()
        }
    }
}
