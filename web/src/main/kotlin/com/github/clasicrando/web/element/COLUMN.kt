package com.github.clasicrando.web.element

import com.github.clasicrando.web.component.Component
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlInlineTag
import kotlinx.html.TagConsumer
import kotlinx.html.visitAndFinalize

private fun String?.takeIfNotNullOrBlank() = this.takeIf { !it.isNullOrBlank() }

private fun Int?.sizeSuffixOrEmpty(): String = this?.let { "-$it" } ?: ""

class COLUMN(
    classes: String? = null,
    size: Int? = null,
    consumer: TagConsumer<*>,
) : DIV(
        initialAttributes =
            mapOf(
                "class" to "col${size.sizeSuffixOrEmpty()} ${classes.takeIfNotNullOrBlank() ?: ""}",
            ),
        consumer = consumer,
    ),
    HtmlInlineTag

@Component
inline fun <T, C : TagConsumer<T>> C.Column(
    classes: String? = null,
    size: Int? = null,
    crossinline block: DIV.() -> Unit,
) {
    COLUMN(classes, size, this).visitAndFinalize(this) { block() }
}

@Component
inline fun FlowContent.Column(
    classes: String? = null,
    size: Int? = null,
    crossinline block: DIV.() -> Unit,
) {
    COLUMN(classes, size, this.consumer).visitAndFinalize(this.consumer) { block() }
}
