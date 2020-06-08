package org.patternfly

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.visit
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ dsl

/**
 * Creates a sidebar component with nested sidebar body.
 * The provided [block] is executed in the context of the sidebar body.
 */
@HtmlTagMarker
fun FlowContent.pfSidebar(dark: Boolean = true, block: SidebarTag.() -> Unit = {}) =
    SidebarTag(dark, consumer).visitPf {
        div("page".component("sidebar", "body")) {
            this@visitPf.block()
        }
    }

/** Creates a sidebar component only w/o a nested sidebar body. */
@HtmlTagMarker
fun FlowContent.pfSidebarOnly(dark: Boolean = true, block: SidebarTag.() -> Unit = {}) =
    SidebarTag(dark, consumer).visitPf(block)

@HtmlTagMarker
fun SidebarTag.pfSidebarBody(block: DIV.() -> Unit = {}) =
    DIV(attributesMapOf("class", "page".component("sidebar", "body")), consumer).visit(block)

// ------------------------------------------------------ tag

class SidebarTag(internal val dark: Boolean = true, consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", buildString {
        append("page".component("sidebar"))
        if (dark) append(" ${"dark".modifier()}")
    }), consumer),
    PatternFlyTag, Ouia {
    override val componentType: ComponentType = ComponentType.Sidebar
}

// ------------------------------------------------------ component

fun EventTarget?.pfSidebar(): SidebarComponent = (this as Element).pfSidebar()

fun Element?.pfSidebar(): SidebarComponent =
    component(this, ComponentType.Sidebar, { document.create.div() }, { it as HTMLDivElement }, ::SidebarComponent)

class SidebarComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {
    fun body(): Element? = element.querySelector(".${"page".component("sidebar", "body")}")
}
