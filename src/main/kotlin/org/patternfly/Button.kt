package org.patternfly

import kotlinx.html.*

@HtmlTagMarker
fun FlowOrInteractiveOrPhrasingContent.pfButton(
    text: String? = null,
    style: Style,
    block: PfButton.() -> Unit = {}
) {
    PfButton(setOf(style.realValue), consumer).visit {
        block(this)
        text?.let { +text }
    }
}

@ExperimentalStdlibApi
fun FlowOrInteractiveOrPhrasingContent.pfLinkButton(
    text: String? = null,
    inline: Boolean = false,
    block: PfButton.() -> Unit = {}
) {
    PfButton(
        buildSet {
            add("link")
            if (inline) add("inline")
        },
        consumer
    ).visit {
        block(this)
        text?.let { +text }
    }
}

class PfButton(modifier: Set<String>, consumer: TagConsumer<*>) :
    BUTTON(
        attributesMapOf("class", buildString {
            append(component("button"))
            if (modifier.isNotEmpty()) append(" ")
            modifier.joinTo(this, " ", transform = { modifier(it) })
        }),
        consumer
    )

enum class Style(override val realValue: String) : AttributeEnum {
    primary(modifier("primary")),
    secondary(modifier("secondary")),
    tertiary(modifier("tertiary")),
    danger(modifier("danger")),
}
