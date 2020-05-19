package org.patternfly

import kotlinx.html.*
import org.w3c.dom.HTMLElement

// ------------------------------------------------------ dsl functions

@HtmlTagMarker
fun FlowOrPhrasingContent.pfIcon(iconClass: String, block: IconTag.() -> Unit = {}): Unit =
    IconTag(iconClass, consumer).visit(block)

// ------------------------------------------------------ tags

class IconTag(iconClass: String, consumer: TagConsumer<*>) : I(attributesMapOf("class", iconClass), consumer),
    PatternFlyTag, Aria, Ouia {
    override val componentType: ComponentType = ComponentType.Icon

    override fun head() {
        aria["hidden"] = true
    }
}

// ------------------------------------------------------ components

class IconComponent(element: HTMLElement) : PatternFlyComponent<HTMLElement>(element)