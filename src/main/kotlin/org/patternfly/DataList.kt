package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import org.jboss.elemento.removeChildren
import org.patternfly.ComponentType.DataList
import org.w3c.dom.Element
import org.w3c.dom.HTMLUListElement
import kotlin.browser.document

typealias DataListRenderer<T> = (item: T, dataProvider: DataProvider<T>) -> DataListItemTag.() -> Unit

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowContent.pfDataList(block: DataListTag.() -> Unit = {}) = DataListTag(consumer).visit(block)

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfItem(block: DataListItemTag.() -> Unit = {}): T =
    DataListItemTag(this).visitAndFinalize(this, block)

@HtmlTagMarker
fun DataListTag.pfItem(block: DataListItemTag.() -> Unit = {}) = DataListItemTag(consumer).visit(block)

@HtmlTagMarker
fun DataListItemTag.pfItemRow(block: DataListItemRowTag.() -> Unit = {}) = DataListItemRowTag(consumer).visit(block)

@HtmlTagMarker
fun DataListItemRowTag.pfItemControl(block: DataListItemControlTag.() -> Unit = {}) =
    DataListItemControlTag(consumer).visit(block)

@HtmlTagMarker
fun DataListItemRowTag.pfItemContent(block: DataListItemContentTag.() -> Unit = {}) =
    DataListItemContentTag(consumer).visit(block)

@HtmlTagMarker
fun DataListItemContentTag.pfCell(block: DataListCellTag.() -> Unit = {}) = DataListCellTag(consumer).visit(block)

@HtmlTagMarker
fun DataListItemRowTag.pfItemAction(block: DataListItemActionTag.() -> Unit = {}) =
    DataListItemActionTag(consumer).visit(block)

// ------------------------------------------------------ tag

class DataListTag(consumer: TagConsumer<*>) :
    UL(attributesMapOf("class", "data-list".component(), "role", "list"), consumer),
    PatternFlyTag, Ouia {

    override val componentType: ComponentType = DataList
}

class DataListItemTag(consumer: TagConsumer<*>) :
    LI(attributesMapOf("class", "data-list".component("item")), consumer)

class DataListItemRowTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "data-list".component("item-row")), consumer)

class DataListItemControlTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "data-list".component("item-control")), consumer)

class DataListItemContentTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "data-list".component("item-content")), consumer)

class DataListCellTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "data-list".component("cell")), consumer)

class DataListItemActionTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "data-list".component("item-action")), consumer)

// ------------------------------------------------------ component

fun <T> Element.pfDataList(dataProvider: DataProvider<T>, renderer: DataListRenderer<T>): DataListComponent<T> =
    component(
        this,
        DataList,
        { document.create.ul() as HTMLUListElement },
        { it as HTMLUListElement },
        { DataListComponent(it, dataProvider, renderer) }
    )

class DataListComponent<T>(
    element: HTMLUListElement,
    private val dataProvider: DataProvider<T>,
    private val renderer: DataListRenderer<T>
) :
    PatternFlyComponent<HTMLUListElement>(element), Display<T> {

    override fun showItems(items: List<T>, pageInfo: PageInfo) {
        element.removeChildren()
        for (item in items) {
            val id = dataProvider.identifier(item)
            element.append.pfItem {
                aria["labelledby"] = id
                renderer(item, dataProvider)(this)
            }
        }
    }

    override fun updateSelection(selectionInfo: SelectionInfo<T>) {

    }

    override fun updateSortInfo(sortInfo: SortInfo<T>) {

    }
}