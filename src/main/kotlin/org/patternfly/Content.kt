package org.patternfly

import kotlinx.html.*
import org.w3c.dom.HTMLDivElement

// ------------------------------------------------------ dsl functions

@HtmlTagMarker
fun FlowContent.pfContent(block: ContentTag.() -> Unit = {}) {
    ContentTag(consumer).visit(block)
}

// ------------------------------------------------------ tags

class ContentTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "content".component()), consumer), PatternFlyTag, Ouia {
    override val componentType: ComponentType = ComponentType.Content
}

// ------------------------------------------------------ components

class ContentComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
