package com.github.clasicrando.web.component

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter

fun Any?.displayValue(): String = when (this) {
    null -> "-"
    is Iterable<*> -> this.joinToString()
    is Array<*> -> this.joinToString()
    is OffsetDateTime -> this.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    is OffsetTime -> this.format(DateTimeFormatter.ISO_OFFSET_TIME)
    is LocalDateTime -> this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    is LocalDate -> this.format(DateTimeFormatter.ISO_LOCAL_DATE)
    is LocalTime -> this.format(DateTimeFormatter.ISO_LOCAL_TIME)
    else -> this.toString()
}
