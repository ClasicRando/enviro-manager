package com.github.clasicrando.web.element

import kotlinx.html.HTML
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlInlineTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.visitAndFinalize

class NOSCRIPT(consumer: TagConsumer<*>)
    : HTMLTag(
        tagName = "noscript",
        consumer = consumer,
        initialAttributes = emptyMap(),
        inlineTag = true,
        emptyTag = false,
    ), HtmlInlineTag

@HtmlTagMarker
fun HTML.noscript(content: String) {
    NOSCRIPT(this.consumer).visitAndFinalize(this.consumer) { +content }
}
