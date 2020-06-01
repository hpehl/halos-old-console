package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.div
import org.patternfly.ComponentType.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun HeaderTag.pfBrand(block: DIV.() -> Unit = {}) {
    DIV(attributesMapOf("class", "page".component("header", "brand")), consumer).visit(block)
}

@HtmlTagMarker
fun FlowContent.pfBrandLink(homeLink: String, block: A.() -> Unit = {}) {
    A(attributesMapOf("class", "page".component("header", "brand", "link"), "href", homeLink), consumer).visit(block)
}

@HtmlTagMarker
fun PageTag.pfHeader(block: HeaderTag.() -> Unit = {}) {
    HeaderTag(consumer).visit(block)
}

@HtmlTagMarker
fun HeaderTag.pfHeaderTools(block: DIV.() -> Unit = {}) {
    DIV(attributesMapOf("class", "page".component("header", "tools")), consumer).visit(block)
}

@HtmlTagMarker
fun PageTag.pfMain(id: String, block: MAIN.() -> Unit = {}) {
    MAIN(
        attributesMapOf("class", "page".component("main"), "id", id, "role", "main", "tabindex", "-1"),
        consumer
    ).visit(block)
}

@HtmlTagMarker
fun PageTag.pfPage(block: PageTag.() -> Unit = {}) {
    PageTag(consumer).visit(block)
}

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfPage(block: PageTag.() -> Unit = {}): T =
    PageTag(this).visitAndFinalize(this, block)

@HtmlTagMarker
fun MainTag.pfSection(classes: String? = null, block: SECTION.() -> Unit = {}) {
    SECTION(
        attributesMapOf("class", buildString {
            append("page".component("main-section"))
            if (classes != null) append(" $classes")
        }),
        consumer
    ).visit(block)
}

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfSection(classes: String? = null, block: SECTION.() -> Unit = {}): T =
    SECTION(
        attributesMapOf("class", buildString {
            append("page".component("main-section"))
            if (classes != null) append(" $classes")
        }),
        this
    ).visitAndFinalize(this, block)

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

class PageTag(consumer: TagConsumer<*>) :
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
