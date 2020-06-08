package org.patternfly

import kotlinx.html.FlowContent
import kotlinx.html.H1
import kotlinx.html.H2
import kotlinx.html.H3
import kotlinx.html.H4
import kotlinx.html.H5
import kotlinx.html.H6
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.attributesMapOf
import kotlinx.html.visit

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowContent.pfTitle(text: String, level: Int = 1, size: Size = Size._2xl, block: HTMLTag.() -> Unit = {}) {
    val attributes = attributesMapOf("class", "${"title".component()} ${size.modifier}")
    val tag: HTMLTag = when (level) {
        1 -> H1(attributes, consumer)
        2 -> H2(attributes, consumer)
        3 -> H3(attributes, consumer)
        4 -> H4(attributes, consumer)
        5 -> H5(attributes, consumer)
        6 -> H6(attributes, consumer)
        else -> H1(attributes, consumer)
    }
    tag.visit {
        +text
        block(this)
    }
}
