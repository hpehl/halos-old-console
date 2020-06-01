package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.patternfly.ComponentType.Drawer
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowContent.pfDrawer(block: DrawerTag.() -> Unit = {}) = DrawerTag(consumer).visit(block)

@HtmlTagMarker
fun DrawerTag.pfDrawerSection(block: DIV.() -> Unit = {}) =
    DIV(attributesMapOf("class", "drawer".component("section")), consumer).visit(block)

@HtmlTagMarker
fun DrawerTag.pfDrawerMain(block: DrawerMainTag.() -> Unit = {}) = DrawerMainTag(consumer).visit(block)

@HtmlTagMarker
fun DrawerMainTag.pfDrawerContent(block: DrawerContentTag.() -> Unit = {}) = DrawerContentTag(consumer).visit(block)

@HtmlTagMarker
fun DrawerMainTag.pfDrawerPanel(block: DrawerPanelTag.() -> Unit = {}) = DrawerPanelTag(consumer).visit(block)

@HtmlTagMarker
fun DrawerContentTag.pfDrawerBody(block: DrawerBodyTag.() -> Unit = {}) = DrawerBodyTag(consumer).visit(block)

@HtmlTagMarker
fun DrawerPanelTag.pfDrawerBody(block: DrawerBodyTag.() -> Unit = {}) = DrawerBodyTag(consumer).visit(block)

@HtmlTagMarker
fun DrawerBodyTag.pfDrawerHead(block: DrawerHeadTag.() -> Unit = {}) = DrawerHeadTag(consumer).visit(block)

@HtmlTagMarker
fun DrawerHeadTag.pfDrawerActions(block: DrawerActionsTag.() -> Unit = {}) = DrawerActionsTag(consumer).visit(block)

@HtmlTagMarker
fun DrawerActionsTag.pfDrawerClose() {
    div("drawer".component("close")) {
        pfPlainButton(iconClass = "times".fas()) {
            aria["label"] = "Close drawer panel"
            onClickFunction = { it.target.pfDrawer().close() }
        }
    }
}

// ------------------------------------------------------ tag

class DrawerTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "drawer".component()), consumer),
    PatternFlyTag, Ouia {
    override val componentType: ComponentType = Drawer
}

class DrawerMainTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "drawer".component("main")), consumer)

class DrawerContentTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "drawer".component("content")), consumer)

class DrawerPanelTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "drawer".component("panel")), consumer)

class DrawerBodyTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "drawer".component("body")), consumer)

class DrawerHeadTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "drawer".component("head")), consumer)

class DrawerActionsTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "drawer".component("actions")), consumer)

class DrawerCloseTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "drawer".component("close")), consumer)

// ------------------------------------------------------ component

fun EventTarget?.pfDrawer(): DrawerComponent = (this as Element).pfDrawer()

fun Element?.pfDrawer(): DrawerComponent =
    component(this, Drawer, { document.create.div() }, { it as HTMLDivElement }, ::DrawerComponent)

class DrawerComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {
    fun close() {

    }
}
