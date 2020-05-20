package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.div
import org.patternfly.ComponentType.Content
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowContent.pfContent(block: ContentTag.() -> Unit = {}) {
    ContentTag(consumer).visit(block)
}

// ------------------------------------------------------ tag

class ContentTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "content".component()), consumer), PatternFlyTag, Ouia {
    override val componentType: ComponentType = Content
}

// ------------------------------------------------------ component

fun EventTarget.pfContent(): ContentComponent = (this as Element).pfContent()

fun Element.pfContent(): ContentComponent =
    component(this, Content, { document.create.div() }, { it as HTMLDivElement }, ::ContentComponent)

class ContentComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
