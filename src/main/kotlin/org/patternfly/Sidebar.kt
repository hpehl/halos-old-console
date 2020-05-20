package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.div
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowContent.pfSidebar(block: SidebarTag.() -> Unit = {}) = SidebarTag(consumer).visit {
    div("page".component("sidebar", "body")) {
        this@visit.block()
    }
}

@HtmlTagMarker
fun FlowContent.pfSidebarOnly(block: SidebarTag.() -> Unit = {}) = SidebarTag(consumer).visit(block)

@HtmlTagMarker
fun SidebarTag.pfSidebarBody(block: DIV.() -> Unit = {}) =
    DIV(attributesMapOf("class", "page".component("sidebar", "body")), consumer).visit(block)

// ------------------------------------------------------ tag

class SidebarTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "page".component("sidebar")), consumer), PatternFlyTag, Ouia {
    override val componentType: ComponentType = ComponentType.Sidebar
}

// ------------------------------------------------------ component

fun Element.pfSidebar(): SidebarComponent =
    component(this, ComponentType.Sidebar, { document.create.div() }, { it as HTMLDivElement }, ::SidebarComponent)

class SidebarComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {
    fun body(): Element? = element.querySelector(".${"page".component("sidebar", "body")}")
}

