package org.patternfly

import kotlinx.html.*
import org.w3c.dom.HTMLButtonElement

// ------------------------------------------------------ dsl functions

@HtmlTagMarker
@OptIn(ExperimentalStdlibApi::class)
fun FlowOrInteractiveOrPhrasingContent.pfButton(
    style: Style,
    text: String? = null,
    iconClass: String? = null,
    iconRight: Boolean = false,
    blockLevel: Boolean = false,
    block: ButtonTag.() -> Unit = {}
) {
    ButtonTag(buildSet {
        add(style.realValue)
        if (blockLevel) add("block".modifier())
    }, text, iconClass, iconRight, consumer).visit(block)
}

@HtmlTagMarker
fun FlowOrInteractiveOrPhrasingContent.pfControlButton(
    text: String? = null,
    iconClass: String? = null,
    iconRight: Boolean = false,
    block: ButtonTag.() -> Unit = {}
) {
    ButtonTag(setOf("control".modifier()), text, iconClass, iconRight, consumer).visit(block)
}

@HtmlTagMarker
@OptIn(ExperimentalStdlibApi::class)
fun FlowOrInteractiveOrPhrasingContent.pfLinkButton(
    text: String? = null,
    iconClass: String? = null,
    iconRight: Boolean = false,
    inline: Boolean = false,
    block: ButtonTag.() -> Unit = {}
) {
    ButtonTag(buildSet {
        add("link".modifier())
        if (inline) add("inline".modifier())
    }, text, iconClass, iconRight, consumer).visit(block)
}

@HtmlTagMarker
fun FlowOrInteractiveOrPhrasingContent.pfPlainButton(
    text: String? = null,
    iconClass: String? = null,
    iconRight: Boolean = false,
    block: ButtonTag.() -> Unit = {}
) {
    ButtonTag(setOf("plain".modifier()), text, iconClass, iconRight, consumer).visit(block)
}

// ------------------------------------------------------ tags

class ButtonTag(
    modifier: Set<String>,
    private val text: String? = null,
    private val iconClass: String? = null,
    private val iconRight: Boolean = false,
    consumer: TagConsumer<*>
) : BUTTON(
    attributesMapOf("class", buildString {
        append("button".component())
        if (modifier.isNotEmpty()) modifier.joinTo(this, " ", " ")
    }),
    consumer
), PatternFlyTag, Aria, Ouia {

    override val componentType: ComponentType = ComponentType.Button

    override fun head() {
        when {
            text != null && iconClass != null -> if (iconRight) {
                span("button".component("text")) { +this@ButtonTag.text }
                span("button".component("icon")) { pfIcon(this@ButtonTag.iconClass) }
            } else {
                span("button".component("icon")) { pfIcon(this@ButtonTag.iconClass) }
                span("button".component("text")) { +this@ButtonTag.text }
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

// ------------------------------------------------------ components

class ButtonComponent(element: HTMLButtonElement) : PatternFlyComponent<HTMLButtonElement>(element)
