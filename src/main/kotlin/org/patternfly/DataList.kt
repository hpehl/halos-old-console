package org.patternfly

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.dom.create
import kotlinx.html.js.div
import org.patternfly.ComponentType.DataList
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document

// ------------------------------------------------------ dsl

fun FlowContent.pfDataList(block: DataListTag.() -> Unit = {}) = DataListTag(consumer).visit(block)

// ------------------------------------------------------ tag

class DataListTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "data-list".component(), "role", "list"), consumer),
    PatternFlyTag, Ouia {

    override val componentType: ComponentType = DataList
}

// ------------------------------------------------------ component

fun Element.pfDataList(): DataListComponent =
    component(this, DataList, { document.create.div() }, { it as HTMLDivElement }, ::DataListComponent)

class DataListComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element)