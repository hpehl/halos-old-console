package org.patternfly

import kotlinx.html.*

@HtmlTagMarker
fun FlowOrPhrasingContent.pfIcon(iconClass: String, block: PfIcon.() -> Unit = {}): Unit =
    PfIcon(iconClass, consumer).visit {
        block.invoke(this)
        aria["hidden"] = true
        ouiaComponent("Icon")
    }

class PfIcon(iconClass: String, consumer: TagConsumer<*>) : I(attributesMapOf("class", iconClass), consumer), Aria, Ouia
