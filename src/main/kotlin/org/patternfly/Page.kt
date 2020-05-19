package org.patternfly

import kotlinx.html.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

// ------------------------------------------------------ dsl functions

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
fun MainTag.pfSection(classes: String? = null, block: PageSectionTag.() -> Unit = {}) {
    PageSectionTag(classes, consumer).visit(block)
}

// ------------------------------------------------------ tags

class HeaderTag(consumer: TagConsumer<*>) :
    HEADER(
        attributesMapOf(
            "class", "page".component("header"),
            "role", "banner"
        ),
        consumer
    ), PatternFlyTag, Ouia {
    override val componentType: ComponentType = ComponentType.PageHeader
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
    override val componentType: ComponentType = ComponentType.PageMain
}

class PageTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "page".component()), consumer), PatternFlyTag, Ouia {
    override val componentType: ComponentType = ComponentType.Page
}

class PageSectionTag(classes: String? = null, consumer: TagConsumer<*>) :
    kotlinx.html.SECTION(
        attributesMapOf("class", buildString {
            append("page".component("main-section"))
            if (classes != null) append(" $classes")
        }),
        consumer
    ), PatternFlyTag, Ouia {
    override val componentType: ComponentType = ComponentType.PageSection
}

// ------------------------------------------------------ components

class HeaderComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageMainComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageHeaderComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)
class PageSectionComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)