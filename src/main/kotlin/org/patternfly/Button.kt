package org.patternfly

import kotlinx.html.*
import org.w3c.dom.HTMLDivElement

@HtmlTagMarker
fun <T> TagConsumer<T>.pfButton(block: PfButton.() -> Unit = {}): HTMLDivElement =
    PfButton(this).visitAndFinalize(this, block) as HTMLDivElement

class PfButton(consumer: TagConsumer<*>) :
    BUTTON(
        attributesMapOf("class", component("button")),
        consumer
    )
