package com.github.clasicrando.web.element

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlInlineTag
import kotlinx.html.TagConsumer
import kotlinx.html.visitAndFinalize

private fun String?.takeIfNotNullOrBlank() = this.takeIf { !it.isNullOrBlank() }

private fun Int?.sizeSuffixOrEmpty(): String = this?.let { "-$it" } ?: ""

class COLUMN(classes: String? = null, size: Int? = null, consumer: TagConsumer<*>) :
    DIV(
        initialAttributes =
            mapOf(
                "class" to "col${size.sizeSuffixOrEmpty()} ${classes.takeIfNotNullOrBlank() ?: ""}",
            ),
        consumer = consumer,
    ),
    HtmlInlineTag

inline fun <T, C : TagConsumer<T>> C.column(
    classes: String? = null,
    size: Int? = null,
    crossinline block: DIV.() -> Unit,
) {
    COLUMN(classes, size, this).visitAndFinalize(this) { block() }
}

inline fun FlowContent.column(
    classes: String? = null,
    size: Int? = null,
    crossinline block: DIV.() -> Unit,
) {
    COLUMN(classes, size, this.consumer).visitAndFinalize(this.consumer) { block() }
}
