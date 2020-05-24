package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import org.jboss.elemento.aria
import org.jboss.elemento.removeChildren
import org.patternfly.ComponentType.DataList
import org.patternfly.Data.DATA_LIST_RENDERER
import org.w3c.dom.Element
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.get
import kotlin.browser.document

typealias DataListRenderer<T> = (item: T, dataProvider: DataProvider<T>) -> DataListItemTag.() -> Unit

private val rendererRegistry: MutableMap<String, DataListRenderer<*>> = mutableMapOf()

private fun <T> noopRenderer(id: String): DataListRenderer<T> = { _, _ ->
    {
        console.log("No data list renderer defined for $id")
    }
}

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun <T> FlowContent.pfDataList(id: String, block: DataListTag<T>.() -> Unit = {}) =
    DataListTag<T>(id, consumer).visit(block)

@HtmlTagMarker
private fun <T, C : TagConsumer<T>> C.pfItem(block: DataListItemTag.() -> Unit = {}): T =
    DataListItemTag(this).visitAndFinalize(this, block)

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

class DataListTag<T>(id: String, consumer: TagConsumer<*>) :
    UL(attributesMapOf("class", "data-list".component(), "role", "list"), consumer),
    PatternFlyTag, Ouia {

    override val componentType: ComponentType = DataList

    var renderer: DataListRenderer<T> = noopRenderer(id)
        set(value) {
            field = value
            attributes[DATA_LIST_RENDERER] = id
            rendererRegistry[id] = renderer.unsafeCast<DataListRenderer<*>>()
        }
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

fun <T> Element.pfDataList(dataProvider: DataProvider<T>): DataListComponent<T> =
    component(
        this,
        DataList,
        { document.create.ul() as HTMLUListElement },
        { it as HTMLUListElement },
        { DataListComponent(it, dataProvider) }
    )

class DataListComponent<T>(element: HTMLUListElement, override val dataProvider: DataProvider<T>) :
    PatternFlyComponent<HTMLUListElement>(element), Display<T> {

    private var id: String = "n/a"
    private var renderer: DataListRenderer<T>? = null

    init {
        id = element.id
        val dlrId = element.dataset["dlr"]
        if (dlrId != null) {
            renderer = rendererRegistry[dlrId].unsafeCast<DataListRenderer<T>>()
        }
    }

    override fun showItems(items: List<T>, pageInfo: PageInfo) {
        element.removeChildren()
        if (renderer != null) {
            for (item in items) {
                val itemElement = element.append.pfItem(renderer!!.invoke(item, dataProvider))
                val itemId = dataProvider.identifier(item)
                if (itemElement.querySelector("#$itemId") != null) {
                    itemElement.aria["labelledby"] = itemId
                }
            }
        } else {
            console.log("No data list renderer found for $id")
        }
    }

    override fun updateSelection(selectionInfo: SelectionInfo<T>) {

    }

    override fun updateSortInfo(sortInfo: SortInfo<T>) {

    }
}