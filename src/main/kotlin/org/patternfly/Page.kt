package org.patternfly

import kotlinx.html.*

// ------------------------------------------------------ component functions (a-z)

@HtmlTagMarker
fun PfHeader.pfBrand(block: PfBrand.() -> Unit = {}) {
    PfBrand(consumer).visit {
        block(this)
        ouiaComponent("PageHeaderBrand")
    }
}

@HtmlTagMarker
fun PfSection.pfContent(block: PfContent.() -> Unit = {}) {
    PfContent(consumer).visit {
        block(this)
        ouiaComponent("PageContent")
    }
}

@HtmlTagMarker
fun PfPage.pfHeader(block: PfHeader.() -> Unit = {}) {
    PfHeader(consumer).visit {
        block(this)
        ouiaComponent("PageHeader")
    }
}

@HtmlTagMarker
fun PfPage.pfMain(id: String, block: PfMain.() -> Unit = {}) {
    PfMain(id, consumer).visit {
        block(this)
        ouiaComponent("PageMain")
    }
}

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfPage(block: PfPage.() -> Unit = {}): T {
    return PfPage(this).visitAndFinalize(this, block)
}

@HtmlTagMarker
fun PfMain.pfSection(block: PfSection.() -> Unit = {}) {
    PfSection(consumer).visit {
        block(this)
        ouiaComponent("PageSection")
    }
}

// ------------------------------------------------------ component classes (a-z)

class PfBrand(consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", "page".component("header", "brand")),
        consumer
    ), Ouia

class PfContent(consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", "content".component()),
        consumer
    ), Ouia

class PfHeader(consumer: TagConsumer<*>) :
    HEADER(
        attributesMapOf(
            "class", "page".component("header"),
            "role", "banner"
        ),
        consumer
    ), Ouia

class PfMain(id: String, consumer: TagConsumer<*>) :
    MAIN(
        attributesMapOf(
            "class", "page".component("main"),
            "id", id,
            "role", "main",
            "tabindex", "-1"
        ),
        consumer
    ), Ouia

class PfPage(consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", "page".component()),
        consumer
    ), Ouia

class PfSection(consumer: TagConsumer<*>) :
    kotlinx.html.SECTION(
        attributesMapOf("class", "page".component("main-section")),
        consumer
    ), Ouia
