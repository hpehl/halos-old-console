package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.div
import org.patternfly.ComponentType.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun HeaderTag.pfBrand(block: DIV.() -> Unit = {}) {
    DIV(attributesMapOf("class", "page".component("header", "brand")), consumer).visit(block)
}

@HtmlTagMarker
fun PageTag.pfHeader(block: HeaderTag.() -> Unit = {}) {
    HeaderTag(consumer).visit(block)
}

@HtmlTagMarker
fun PageTag.pfMain(id: String, block: MainTag.() -> Unit = {}) {
    MainTag(id, consumer).visit(block)
}

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfPage(block: PageTag.() -> Unit = {}): T =
    PageTag(this).visitAndFinalize(this, block)

@HtmlTagMarker
fun MainTag.pfSection(classes: String? = null, block: SectionTag.() -> Unit = {}) {
    SectionTag(classes, consumer).visit(block)
}

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
        attributesMapOf(
            "class", "page".component("main"),
            "id", id,
            "role", "main",
            "tabindex", "-1"
        ),
        consumer
    ), PatternFlyTag, Ouia {
    override val componentType: ComponentType = PageMain
}

class PageTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "page".component()), consumer), PatternFlyTag, Ouia {
    override val componentType: ComponentType = Page
}

class SectionTag(classes: String? = null, consumer: TagConsumer<*>) :
    kotlinx.html.SECTION(
        attributesMapOf("class", buildString {
            append("page".component("main-section"))
            if (classes != null) append(" $classes")
        }),
        consumer
    ), PatternFlyTag, Ouia {
    override val componentType: ComponentType = PageSection
}

// ------------------------------------------------------ component

fun Element.pfHeader(): PageHeaderComponent =
    component(this, PageHeader, { document.create.div() }, { it as HTMLDivElement }, ::PageHeaderComponent)

fun Element.pfMain(): PageMainComponent =
    component(this, PageMain, { document.create.div() }, { it as HTMLDivElement }, ::PageMainComponent)

fun Element.pfPage(): PageComponent =
    component(this, Page, { document.create.div() }, { it as HTMLDivElement }, ::PageComponent)

fun Element.pfSection(): PageSectionComponent =
    component(this, PageSection, { document.create.div() }, { it as HTMLDivElement }, ::PageSectionComponent)

class PageComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageMainComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageHeaderComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageSectionComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
