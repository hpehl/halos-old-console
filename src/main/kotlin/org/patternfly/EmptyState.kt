package org.patternfly

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.visit
import org.patternfly.ComponentType.EmptyState
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowContent.pfEmptyState(iconClass: String, title: String, block: EmptyStateTag.() -> Unit = {}) {
    EmptyStateTag(consumer).visitPf {
        div("empty-state".component("content")) {
            pfIcon(iconClass) {
                classes += "empty-state".component("icon")
            }
            pfTitle(title, size = Size.lg)
            this@visitPf.block()
        }
    }
}

@HtmlTagMarker
fun EmptyStateTag.pfEmptyStateBody(block: DIV.() -> Unit = {}) {
    DIV(attributesMapOf("class", "empty-state".component("body")), consumer).visit(block)
}

@HtmlTagMarker
fun EmptyStateTag.pfEmptyStateSecondary(block: DIV.() -> Unit = {}) {
    DIV(attributesMapOf("class", "empty-state".component("secondary")), consumer).visit(block)
}

// ------------------------------------------------------ tag

class EmptyStateTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "empty-state".component()), consumer),
    PatternFlyTag, Ouia {
    override val componentType: ComponentType = EmptyState
}

// ------------------------------------------------------ component

fun EventTarget?.pfEmptyState(): EmptyStateComponent = (this as Element).pfEmptyState()

fun Element?.pfEmptyState(): EmptyStateComponent =
    component(this, EmptyState, { document.create.div() }, { it as HTMLDivElement }, ::EmptyStateComponent)

class EmptyStateComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
