package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import org.jboss.elemento.aria
import org.patternfly.ComponentType.DataList
import org.w3c.dom.Element
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.asList
import kotlin.browser.document
import kotlin.collections.set
import kotlin.dom.clear

typealias DataListRenderer<T> = (T, DataProvider<T>) -> DataListItemTag.() -> Unit

private val dataListRendererRegistry: MutableMap<String, DataListRenderer<*>> = mutableMapOf()

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun <T> FlowContent.pfDataList(block: DataListTag<T>.() -> Unit = {}) =
    DataListTag<T>(consumer).visit(block)

@HtmlTagMarker
private fun <T, C : TagConsumer<T>> C.pfDataListItem(block: DataListItemTag.() -> Unit = {}): T =
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

class DataListTag<T>(consumer: TagConsumer<*>) :
    UL(attributesMapOf("class", "data-list".component(), "role", "list"), consumer),
    PatternFlyTag, Ouia {

    private val id: String = Id.unique()
    override val componentType: ComponentType = DataList

    var renderer: DataListRenderer<T>? = null
        set(value) {
            field = value
            if (value != null) {
                attributes[Dataset.REGISTRY.long] = id
                dataListRendererRegistry[id] = value.unsafeCast<DataListRenderer<*>>()
            }
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

fun <T> Element?.pfDataList(dataProvider: DataProvider<T>): DataListComponent<T> =
    component(
        this,
        DataList,
        { document.create.ul() as HTMLUListElement },
        { it as HTMLUListElement },
        { DataListComponent(it, dataProvider) }
    )

class DataListComponent<T>(element: HTMLUListElement, override val dataProvider: DataProvider<T>) :
    PatternFlyComponent<HTMLUListElement>(element), Display<T> {

    private val renderer: DataListRenderer<T> by RegistryLookup<DataListComponent<T>, DataListRenderer<T>>(
        Dataset.REGISTRY, dataListRendererRegistry
    ) {
        { _, _ ->
            {
                console.error(
                    "No renderer defined for data list ${element.tagName}.${element.classList.asList()
                        .joinToString(".")}"
                )
            }
        }
    }

    override fun showItems(items: List<T>, pageInfo: PageInfo) {
        element.clear()
        for (item in items) {
            element.append {
                pfDataListItem {
                    renderer(item, dataProvider).invoke(this)
                }
            }
            val itemId = dataProvider.identifier(item)
            element.querySelector("#$itemId")?.let {
                it.aria["labelledby"] = itemId
            }
        }
    }

    override fun updateSelection(selectionInfo: SelectionInfo<T>) {

    }

    override fun updateSortInfo(sortInfo: SortInfo<T>) {

    }
}
