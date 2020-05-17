package org.patternfly

import kotlinx.html.*

@HtmlTagMarker
fun FlowOrPhrasingContent.pfIcon(iconClass: String, block: PfIcon.() -> Unit = {}): Unit =
    PfIcon(iconClass, consumer).visit(block)

class PfIcon(iconClass: String, consumer: TagConsumer<*>) : I(attributesMapOf("class", iconClass), consumer)
