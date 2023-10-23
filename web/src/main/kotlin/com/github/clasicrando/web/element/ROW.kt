package com.github.clasicrando.web.element

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlInlineTag
import kotlinx.html.TagConsumer
import kotlinx.html.visitAndFinalize

class ROW(classes: String = "", consumer: TagConsumer<*>) :
    DIV(
        initialAttributes =
            mapOf(
                "class" to if (classes.isBlank()) "row" else "row $classes",
            ),
        consumer = consumer,
    ),
    HtmlInlineTag

fun FlowContent.row(
    classes: String = "",
    block: DIV.() -> Unit,
) {
    ROW(classes, this.consumer).visitAndFinalize(this.consumer) { block() }
}
