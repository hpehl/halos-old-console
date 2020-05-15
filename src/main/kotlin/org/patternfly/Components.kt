package org.patternfly

import kotlinx.html.*
import org.w3c.dom.HTMLDivElement

// ------------------------------------------------------ component functions (a-z)

@HtmlTagMarker
fun PfHeader.pfBrand(block: PfBrand.() -> Unit = {}) = PfBrand(consumer).visit(block)

@HtmlTagMarker
fun PfSection.pfContent(block: PfContent.() -> Unit = {}) = PfContent(consumer).visit(block)

@HtmlTagMarker
fun PfPage.pfHeader(block: PfHeader.() -> Unit = {}) = PfHeader(consumer).visit(block)

@HtmlTagMarker
fun PfPage.pfMain(id: String, block: PfMain.() -> Unit = {}) = PfMain(id, consumer).visit(block)

@HtmlTagMarker
fun <T> TagConsumer<T>.pfPage(block: PfPage.() -> Unit = {}): HTMLDivElement =
    PfPage(this).visitAndFinalize(this, block) as HTMLDivElement

@HtmlTagMarker
fun PfMain.pfSection(block: PfSection.() -> Unit = {}) = PfSection(consumer).visit(block)

// ------------------------------------------------------ component classes (a-z)

class PfBrand(consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", component("page", "header", "brand")),
        consumer
    )

class PfContent(consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", component("content")),
        consumer
    )

class PfHeader(consumer: TagConsumer<*>) :
    HEADER(
        attributesMapOf(
            "class", component("page", "header"),
            "role", "banner"
        ),
        consumer
    )

class PfMain(id: String, consumer: TagConsumer<*>) :
    MAIN(
        attributesMapOf(
            "class", component("page", "main"),
            "id", id,
            "role", "main",
            "tabindex", "-1"
        ),
        consumer
    )

class PfPage(consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", component("page")),
        consumer
    )

class PfSection(consumer: TagConsumer<*>) :
    kotlinx.html.SECTION(
        attributesMapOf("class", component("page", "main-section")),
        consumer
    )
