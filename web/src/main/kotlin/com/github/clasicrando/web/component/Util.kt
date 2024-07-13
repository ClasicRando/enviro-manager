package com.github.clasicrando.web.component

import com.github.clasicrando.web.htmx.hxDelete
import com.github.clasicrando.web.htmx.hxGet
import com.github.clasicrando.web.htmx.hxPatch
import com.github.clasicrando.web.htmx.hxPost
import com.github.clasicrando.web.htmx.hxPut
import io.ktor.http.HttpMethod
import kotlinx.html.FlowContent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Any?.displayValue(): String =
    when (this) {
        null -> "-"
        is Iterable<*> -> this.joinToString()
        is Array<*> -> this.joinToString()
        is OffsetDateTime ->
            this
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        is LocalDateTime -> this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        is LocalDate -> this.format(DateTimeFormatter.ISO_LOCAL_DATE)
        is LocalTime -> this.format(DateTimeFormatter.ISO_LOCAL_TIME)
        else -> this.toString()
    }

fun FlowContent.setHxUrl(
    httpMethod: HttpMethod,
    url: String,
) {
    when (httpMethod) {
        HttpMethod.Get -> hxGet = url
        HttpMethod.Post -> hxPost = url
        HttpMethod.Put -> hxPut = url
        HttpMethod.Patch -> hxPatch = url
        HttpMethod.Delete -> hxDelete = url
    }
}
