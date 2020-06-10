package org.patternfly

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.tabIndex
import kotlinx.html.visit
import kotlinx.html.visitAndFinalize
import org.jboss.elemento.By
import org.jboss.elemento.aria
import org.jboss.elemento.querySelector
import org.patternfly.ComponentType.Drawer
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document
import kotlin.dom.clear

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowContent.pfDrawer(block: DrawerTag.() -> Unit = {}) = DrawerTag(consumer).visitPf(block)

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
fun <T, C : TagConsumer<T>> C.pfDrawerBody(block: DrawerBodyTag.() -> Unit = {}): T =
    DrawerBodyTag(this).visitAndFinalize(this, block)

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
            tabIndex = "-1"
            aria["label"] = "Close drawer panel"
            onClickFunction = { it.target.pfDrawer().collapse() }
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

class DrawerPanelTag(consumer: TagConsumer<*>) : DIV(
    attributesMapOf(
        "class",
        "drawer".component("panel"),
        "aria-hidden",
        true.toString(),
        "aria-expanded",
        false.toString(),
        "hidden",
        true.toString()
    ), consumer
)

class DrawerBodyTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "drawer".component("body")), consumer)

class DrawerHeadTag(consumer: TagConsumer<*>) : DIV(attributesMapOf("class", "drawer".component("head")), consumer)

class DrawerActionsTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "drawer".component("actions")), consumer)

// ------------------------------------------------------ component

fun EventTarget?.pfDrawer(): DrawerComponent = (this as Element).pfDrawer()

fun Element?.pfDrawer(): DrawerComponent =
    component(this, Drawer, { document.create.div() }, { it as HTMLDivElement }, ::DrawerComponent)

class DrawerComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {
    private val panel: HTMLElement = element.querySelector(By.classname("drawer".component("panel"))) as HTMLElement

    fun show(block: TagConsumer<HTMLElement>.() -> Unit) {
        panel.clear()
        panel.append {
            block(this)
        }
        expand()
    }

    fun expand() {
        if (!expanded()) {
            element.classList.add("expanded".modifier())
            with(panel) {
                aria["hidden"] = false
                aria["expanded"] = true
                removeAttribute("hidden")
            }
        }
    }

    fun collapse() {
        if (expanded()) {
            element.classList.remove("expanded".modifier())
            with(panel) {
                aria["hidden"] = true
                aria["expanded"] = false
                setAttribute("hidden", true.toString())
            }
        }
    }

    private fun expanded(): Boolean = element.classList.contains("expanded".modifier())
}
