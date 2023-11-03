package com.github.clasicrando.web.element

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlInlineTag
import kotlinx.html.TagConsumer
import kotlinx.html.visitAndFinalize

class ROW(classes: String? = null, consumer: TagConsumer<*>) :
    DIV(
        initialAttributes =
            mapOf(
                "class" to if (classes.isNullOrBlank()) "row" else "row $classes",
            ),
        consumer = consumer,
    ),
    HtmlInlineTag

inline fun <T, C : TagConsumer<T>> C.row(
    classes: String? = null,
    crossinline block: DIV.() -> Unit,
) {
    ROW(classes, this).visitAndFinalize(this) { block() }
}

inline fun FlowContent.row(
    classes: String? = null,
    crossinline block: DIV.() -> Unit,
) {
    ROW(classes, this.consumer).visitAndFinalize(this.consumer) { block() }
}
