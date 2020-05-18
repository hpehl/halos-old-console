package org.patternfly

import kotlinx.html.*

// ------------------------------------------------------ component functions (a-z)

@HtmlTagMarker
@ExperimentalStdlibApi
fun FlowOrInteractiveOrPhrasingContent.pfButton(
    style: Style,
    text: String? = null,
    iconClass: String? = null,
    iconRight: Boolean = false,
    blockLevel: Boolean = false,
    block: PfButton.() -> Unit = {}
) {
    PfButton(
        buildSet {
            add(style.realValue)
            if (blockLevel) add("block".modifier())
        },
        consumer
    ).visit {
        block(this)
        textOrIcon(text, iconClass, iconRight)
    }
}

@HtmlTagMarker
@ExperimentalStdlibApi
fun FlowOrInteractiveOrPhrasingContent.pfControlButton(
    text: String? = null,
    iconClass: String? = null,
    iconRight: Boolean = false,
    block: PfButton.() -> Unit = {}
) {
    PfButton(setOf("control".modifier()), consumer).visit {
        block(this)
        textOrIcon(text, iconClass, iconRight)
    }
}

@HtmlTagMarker
@ExperimentalStdlibApi
fun FlowOrInteractiveOrPhrasingContent.pfLinkButton(
    text: String? = null,
    iconClass: String? = null,
    iconRight: Boolean = false,
    inline: Boolean = false,
    block: PfButton.() -> Unit = {}
) {
    PfButton(
        buildSet {
            add("link".modifier())
            if (inline) add("inline".modifier())
        },
        consumer
    ).visit {
        block(this)
        textOrIcon(text, iconClass, iconRight)
    }
}

@HtmlTagMarker
@ExperimentalStdlibApi
fun FlowOrInteractiveOrPhrasingContent.pfPlainButton(
    text: String? = null,
    iconClass: String? = null,
    iconRight: Boolean = false,
    block: PfButton.() -> Unit = {}
) {
    PfButton(setOf("plain".modifier()), consumer).visit {
        block(this)
        textOrIcon(text, iconClass, iconRight)
    }
}

// ------------------------------------------------------ component classes

class PfButton(modifier: Set<String>, consumer: TagConsumer<*>) :
    BUTTON(
        attributesMapOf("class", buildString {
            append("button".component())
            if (modifier.isNotEmpty()) modifier.joinTo(this, " ", " ")
        }),
        consumer
    ) {

    init {
        // This line causes an IllegalStateException
//        classes += modifier
    }

    fun textOrIcon(text: String?, iconClass: String?, iconRight: Boolean = false) {
        when {
            text != null && iconClass != null -> if (iconRight) {
                span("button".component("text")) { +text }
                span("button".component("icon")) { pfIcon(iconClass) }
            } else {
                span("button".component("icon")) { pfIcon(iconClass) }
                span("button".component("text")) { +text }
            }
            text != null -> +text
            iconClass != null -> pfIcon(iconClass)
        }
    }
}

enum class Style(override val realValue: String) : AttributeEnum {
    primary("primary".modifier()),
    secondary("secondary".modifier()),
    tertiary("tertiary".modifier()),
    danger("danger".modifier()),
}
