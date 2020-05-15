package org.patternfly

import kotlinx.html.*
import org.w3c.dom.HTMLDivElement

@HtmlTagMarker
fun <T> TagConsumer<T>.page(block: PAGE.() -> Unit = {}): HTMLDivElement =
    PAGE(this).visitAndFinalize(this, block) as HTMLDivElement

@HtmlTagMarker
fun PAGE.header(block: HEADER.() -> Unit = {}) = HEADER(consumer).visit(block)

@HtmlTagMarker
fun HEADER.brand(block: BRAND.() -> Unit = {}) = BRAND(consumer).visit(block)

class PAGE(consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", component("page")),
        consumer
    )

class HEADER(consumer: TagConsumer<*>) :
    kotlinx.html.HEADER(
        attributesMapOf("class", component("page", "header"), "role", "banner"),
        consumer
    )

class BRAND(consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", component("page", "header", "brand")),
        consumer
    )
