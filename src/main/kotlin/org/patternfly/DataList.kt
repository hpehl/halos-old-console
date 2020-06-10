package org.patternfly

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.LI
import kotlinx.html.TagConsumer
import kotlinx.html.UL
import kotlinx.html.attributesMapOf
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.ul
import kotlinx.html.tabIndex
import kotlinx.html.visit
import kotlinx.html.visitAndFinalize
import org.jboss.elemento.By
import org.jboss.elemento.Id
import org.jboss.elemento.aria
import org.jboss.elemento.querySelector
import org.patternfly.ComponentType.DataList
import org.patternfly.Dataset.DATA_LIST_ITEM
import org.w3c.dom.Element
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.asList
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.collections.set
import kotlin.dom.clear

// ------------------------------------------------------ api

typealias DataListRenderer<T> = (T, DataProvider<T>) -> DataListItemTag.() -> Unit

private val dlr: MutableMap<String, DataListRenderer<*>> = mutableMapOf()

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun <T> FlowContent.pfDataList(
    selectionMode: SelectionMode = SelectionMode.NONE,
    block: DataListTag<T>.() -> Unit = {}
) = DataListTag<T>(selectionMode, consumer).visitPf(block)

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

class DataListTag<T>(private val selectionMode: SelectionMode = SelectionMode.NONE, consumer: TagConsumer<*>) :
    UL(attributesMapOf("class", "data-list".component(), "role", "list"), consumer),
    PatternFlyTag, Ouia {

    private val id: String = Id.unique()
    override val componentType: ComponentType = DataList

    @Suppress("UNCHECKED_CAST")
    var renderer: DataListRenderer<T>? = null
        set(value) {
            field = value
            if (value != null) {
                attributes[Dataset.REGISTRY.long] = id
                dlr[id] = value as DataListRenderer<*>
            }
        }

    override fun head() {
        attributes[Dataset.SELECTION_MODE.long] = selectionMode.name.toLowerCase()
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

    private val selectionMode: SelectionMode =
        SelectionMode.valueOf(element.dataset[Dataset.SELECTION_MODE.short]?.toUpperCase() ?: SelectionMode.NONE.name)
    private val renderer: DataListRenderer<T> by RegistryLookup<DataListComponent<T>, DataListRenderer<T>>(dlr) {
        { _, _ ->
            {
                console.error(
                    "No renderer defined for data list <${element.tagName.toLowerCase()}${element.attributes.asList()
                        .joinToString(" ", " ") { """${it.name}="${it.value}"""" }}/>"
                )
            }
        }
    }

    override fun showItems(items: List<T>, pageInfo: PageInfo) {
        element.clear()
        for (item in items) {
            val itemId = dataProvider.identifier(item)
            element.append {
                pfDataListItem {
                    tabIndex = "0"
                    attributes[DATA_LIST_ITEM.long] = itemId
                    if (selectionMode != SelectionMode.NONE) {
                        classes += "selectable".modifier()
                        onClickFunction = {
                            if (selectionMode == SelectionMode.SINGLE) {
                                dataProvider.clearAllSelection()
                            }
                            dataProvider.item(itemId)?.let {
                                dataProvider.select(it, true)
                            }
                        }
                    }
                    val block = renderer(item, dataProvider)
                    block(this)
                }
            }
            element.querySelector(By.id(itemId))?.let {
                it.aria["labelledby"] = itemId
            }
        }
    }

    override fun updateSelection(selectionInfo: SelectionInfo<T>) {
        for (item in dataProvider.visibleItems) {
            val itemId = (dataProvider.identifier)(item)
            element.querySelector(By.data(DATA_LIST_ITEM.long, itemId))?.let {
                if (selectionInfo.selected(item)) {
                    it.classList.add("selected".modifier())
                    it.aria["selected"] = true
                } else {
                    it.classList.remove("selected".modifier())
                    it.removeAttribute("aria-selected")
                }
            }
        }
    }

    override fun updateSortInfo(sortInfo: SortInfo<T>) {
    }
}
