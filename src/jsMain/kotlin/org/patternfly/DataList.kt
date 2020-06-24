package org.patternfly

import dev.fritz2.binding.RootStore
import dev.fritz2.binding.each
import dev.fritz2.binding.handledBy
import dev.fritz2.dom.html.Div
import dev.fritz2.dom.html.HtmlElements
import dev.fritz2.dom.html.Li
import dev.fritz2.dom.html.render
import dev.fritz2.lenses.WithId
import org.jboss.elemento.Id
import org.patternfly.Dataset.DATA_LIST_ITEM
import org.w3c.dom.HTMLUListElement

// ------------------------------------------------------ dsl

typealias DataListDisplay<T> = (T) -> Li.() -> Unit

fun <T : WithId> HtmlElements.pfDataList(
    selectionMode: SelectionMode = SelectionMode.NONE,
    store: DataListStore<T>,
    content: DataList<T>.() -> Unit = {}
): DataList<T> = register(DataList(selectionMode, store), content)

fun HtmlElements.pfDataListItemRow(content: Div.() -> Unit = {}): Div =
    register(Div(baseClass = "data-list".component("item-row")), content)

fun HtmlElements.pfDataListItemControl(content: Div.() -> Unit = {}): Div =
    register(Div(baseClass = "data-list".component("item-control")), content)

fun HtmlElements.pfDataListItemContent(content: Div.() -> Unit = {}): Div =
    register(Div(baseClass = "data-list".component("item-content")), content)

fun HtmlElements.pfDataListItemCell(content: Div.() -> Unit = {}): Div =
    register(Div(baseClass = "data-list".component("cell")), content)

fun HtmlElements.pfDataListItemAction(content: Div.() -> Unit = {}): Div =
    register(Div(baseClass = "data-list".component("item-action")), content)

// ------------------------------------------------------ tag

class DataList<T : WithId> internal constructor(
    private val selectionMode: SelectionMode,
    private val store: DataListStore<T>
) : PatternFlyTag<HTMLUListElement>(ComponentType.DataList, "ul", "data-list".component()), Ouia {

    var identifier: Identifier<T> = { Id.asId(it.id) }
    var asText: AsText<T> = { it.toString() }
    var display: DataListDisplay<T> = {
        {
            text(this@DataList.asText.invoke(it))
        }
    }

    init {
        attr("role", "list")
        store.data.each().map { item ->
            val itemId = identifier(item)
            render {
                li(baseClass = "data-list".component("item")) {
                    attr("tabindex", "0")
                    attr(DATA_LIST_ITEM.long, itemId)
                    if (this@DataList.selectionMode != SelectionMode.NONE) {
                        domNode.classList.add("selectable".modifier())
                        clicks.map { item } handledBy this@DataList.store.selects
                    }
                    val content = this@DataList.display.invoke(item)
                    content.invoke(this)
                }
            }
        }.bind()
    }
}

// ------------------------------------------------------ store

open class DataListStore<T : WithId> : RootStore<List<T>>(listOf()) {
    val selects = handleAndEmit<T, T> { items, item ->
        offer(item)
        items
    }

    val remove = handle<String> { items, id ->
        console.log("Remove $id")
        console.log("Before remove: ${items.joinToString { it.id }}")
        val updated = items.filterNot { it.id == id }
        console.log("After remove: ${updated.joinToString { it.id }}")
        updated
    }
}
