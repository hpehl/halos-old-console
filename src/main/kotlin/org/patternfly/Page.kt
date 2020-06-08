package org.patternfly

import kotlinx.html.A
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HEADER
import kotlinx.html.HtmlTagMarker
import kotlinx.html.MAIN
import kotlinx.html.SECTION
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.visit
import kotlinx.html.visitAndFinalize
import org.patternfly.ComponentType.Page
import org.patternfly.ComponentType.PageHeader
import org.patternfly.ComponentType.PageMain
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun HeaderTag.pfBrand(block: DIV.() -> Unit = {}) {
    DIV(attributesMapOf("class", "page".component("header", "brand")), consumer).visit(block)
}

@HtmlTagMarker
fun FlowContent.pfBrandLink(homeLink: String, block: A.() -> Unit = {}) {
    A(attributesMapOf(
        "class",
        "page".component("header", "brand", "link"),
        "href",
        homeLink
    ), consumer).visit(block)
}

@HtmlTagMarker
fun PageTag.pfHeader(block: HeaderTag.() -> Unit = {}) {
    HeaderTag(consumer).visitPf(block)
}

@HtmlTagMarker
fun HeaderTag.pfHeaderTools(block: DIV.() -> Unit = {}) {
    DIV(attributesMapOf("class", "page".component("header", "tools")), consumer).visit(block)
}

@HtmlTagMarker
fun PageTag.pfMain(id: String, block: MainTag.() -> Unit = {}) {
    MainTag(id, consumer).visitPf(block)
}

@HtmlTagMarker
fun PageTag.pfPage(block: PageTag.() -> Unit = {}) {
    PageTag(consumer).visitPf(block)
}

@HtmlTagMarker
fun TagConsumer<HTMLElement>.pfPage(block: PageTag.() -> Unit = {}): HTMLElement =
    PageTag(this).visitPfAndFinalize(this) {
        block()
    }

@HtmlTagMarker
fun MainTag.pfSection(vararg classes: String, block: SECTION.() -> Unit = {}) {
    SECTION(attributesMapOf("class", "page".component("main-section").append(*classes)), consumer).visit(block)
}

@HtmlTagMarker
fun TagConsumer<HTMLElement>.pfSection(vararg classes: String, block: SECTION.() -> Unit = {}): HTMLElement =
    SECTION(attributesMapOf("class", "page".component("main-section").append(*classes)), this)
        .visitAndFinalize(this, block)

// ------------------------------------------------------ tag

class HeaderTag(consumer: TagConsumer<*>) :
    HEADER(
        attributesMapOf(
            "class", "page".component("header"),
            "role", "banner"
        ),
        consumer
    ), PatternFlyTag, Ouia {
    override val componentType: ComponentType = PageHeader
}

class MainTag(id: String, consumer: TagConsumer<*>) :
    MAIN(
        attributesMapOf("class", "page".component("main"), "id", id, "role", "main", "tabindex", "-1"),
        consumer
    ), PatternFlyTag, Ouia {
    override val componentType: ComponentType = PageMain
}

class PageTag internal constructor(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "page".component()), consumer), PatternFlyTag, Ouia {
    override val componentType: ComponentType = Page
}

// ------------------------------------------------------ component

private val globalHeader: PageHeaderComponent by lazy {
    val selector = "${PageHeader.selector()}[role=banner]"
    document.querySelector(selector).pfHeader()
}

fun Document.pfHeader(): PageHeaderComponent = globalHeader

fun EventTarget?.pfHeader(): PageHeaderComponent = (this as Element).pfHeader()

fun Element?.pfHeader(): PageHeaderComponent =
    component(this, PageHeader, { document.create.div() }, { it as HTMLDivElement }, ::PageHeaderComponent)

fun EventTarget?.pfMain(): PageMainComponent = (this as Element).pfMain()

fun Element?.pfMain(): PageMainComponent =
    component(this, PageMain, { document.create.div() }, { it as HTMLDivElement }, ::PageMainComponent)

fun EventTarget?.pfPage(): PageComponent = (this as Element).pfPage()

fun Element?.pfPage(): PageComponent =
    component(this, Page, { document.create.div() }, { it as HTMLDivElement }, ::PageComponent)

class PageComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageMainComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageHeaderComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
