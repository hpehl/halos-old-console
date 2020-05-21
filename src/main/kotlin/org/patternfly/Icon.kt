package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import org.patternfly.ComponentType.Icon
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowOrPhrasingContent.pfIcon(iconClass: String, block: IconTag.() -> Unit = {}): Unit =
    IconTag(iconClass, consumer).visit(block)

// ------------------------------------------------------ tag

class IconTag(iconClass: String, consumer: TagConsumer<*>) : I(attributesMapOf("class", iconClass), consumer),
    PatternFlyTag, Ouia {
    override val componentType: ComponentType = Icon

    override fun head() {
        aria["hidden"] = true
    }
}

// ------------------------------------------------------ component

fun EventTarget.pfIcon(): IconComponent = (this as Element).pfIcon()

fun Element.pfIcon(): IconComponent = component(this, Icon, { document.create.i() }, { it }, ::IconComponent)

class IconComponent(element: HTMLElement) : PatternFlyComponent<HTMLElement>(element)
