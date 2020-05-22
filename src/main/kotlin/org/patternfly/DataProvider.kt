package org.patternfly

import kotlin.math.max
import kotlin.math.min

typealias Identifier<T> = (T) -> String

typealias SelectHandler<T> = (T) -> Unit

typealias Filter<T> = (T) -> Boolean

private val always: Filter<*> = { _ -> true }

fun <T> Filter<T>.and(other: Filter<in T>): Filter<T> = { this(it) && other(it) }

interface Display<T> {

    fun showItems(items: List<T>, pageInfo: PageInfo)

    fun updateSelection(selectionInfo: SelectionInfo<T>)

    fun updateSortInfo(sortInfo: SortInfo<T>)
}

class PageInfo {

    var pageSize: Int = DEFAULT_PAGE_SIZE
        set(value) {
            field = max(1, value)
        }

    var page: Int = 0
        set(value) {
            field = max(0, value)
        }

    var visible: Int = 0
        set(value) {
            field = min(total, value)
        }

    var total: Int = 0
        set(value) {
            field = value
            page = min(page, pages)
        }

    val pages: Int
        get() {
            var pages = total / pageSize
            if (total % pageSize != 0) {
                pages++
            }
            return max(1, pages)
        }

    val from: Int
        get() = if (total == 0) 0 else page * pageSize + 1

    val to: Int
        get() = min(total, from + pageSize - 1)

    fun reset() {
        page = 0
        visible = 0
        total = 0
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}

class SelectionInfo<T>(val identifier: Identifier<T>) {

    private val mutableSelection: MutableMap<String, T> = mutableMapOf()

    fun add(item: T) = mutableSelection.put(identifier(item), item)

    fun remove(item: T) = mutableSelection.remove(identifier(item))

    fun reset() {
        mutableSelection.clear()
    }

    fun selected(item: T): Boolean = identifier(item) in mutableSelection

    val selection: List<T>
        get() = mutableSelection.values.toList()
}

class SortInfo<T> {

    var id: String = EMPTY_SORT_INFO
    var comparator: Comparator<T>? = null
    var ascending: Boolean = true

    fun reset() {
        id = EMPTY_SORT_INFO
        comparator = null
        ascending = true
    }

    companion object {
        const val EMPTY_SORT_INFO = "empty-sort-info"
    }
}

class DataProvider<T>(val identifier: Identifier<T>) {

    private val displays: MutableList<Display<T>> = mutableListOf()
    private val pageInfo: PageInfo = PageInfo()
    private val selectionInfo: SelectionInfo<T> = SelectionInfo(identifier)
    private val selectHandler: MutableList<SelectHandler<T>> = mutableListOf()
    private val filters: MutableMap<String, Filter<T>> = mutableMapOf()
    private var sortInfo: SortInfo<T> = SortInfo()
    private var allItems: Map<String, T> = linkedMapOf()
    private var filteredItems: Map<String, T> = linkedMapOf()
    private var visibleItems: Map<String, T> = linkedMapOf()

    // ------------------------------------------------------ display

    fun bind(display: Display<T>) {
        displays.add(display)
    }

    // ------------------------------------------------------ items

    fun item(id: String): T? = allItems[id]

    operator fun contains(item: T): Boolean = identifier(item) in allItems

    fun visible(item: T) = identifier(item) in visibleItems

    fun update(items: List<T>) {
        filters.clear()
        pageInfo.reset()
        selectionInfo.reset()
        sortInfo.reset()

        allItems = items.map { identifier(it) to it }.toMap()
        updateInternal()
    }

    private fun updateInternal() {
        var items = allItems.values.toList()
        if (filters.isNotEmpty()) {
            val initial: Filter<T> = { _ -> true }
            val combined = filters.values.fold(initial) { f1, f2 -> f1.and(f2) }
            items = items.filter(combined)
        }
        if (sortInfo.comparator != null) {
            items.sortedWith(sortInfo.comparator!!)
        }
        if (items.size > pageInfo.pageSize) {
            filteredItems = linkedMapOf(*items.map { identifier(it) to it }.toTypedArray())
            items = paged(items)
            visibleItems = linkedMapOf(*items.map { identifier(it) to it }.toTypedArray())
        } else {
            filteredItems = linkedMapOf(*items.map { identifier(it) to it }.toTypedArray())
            visibleItems = filteredItems
        }
        pageInfo.total = filteredItems.size
        pageInfo.visible = visibleItems.size

        for (display in displays) {
            display.showItems(visibleItems.values.toList(), pageInfo)
            display.updateSelection(selectionInfo)
            display.updateSortInfo(sortInfo)
        }
    }

    private fun paged(items: List<T>): List<T> {
        val pages = items.chunked(pageInfo.pageSize)
        val index = min(pageInfo.page, pages.size - 1)
        return pages[index]
    }

    // ------------------------------------------------------ selection

    fun onSelect(handler: SelectHandler<T>) {
        selectHandler.add(handler)
    }

    /** Selects all items. Does not fire selection events. */
    fun selectAll() {
        selectionInfo.reset()
        for (item in filteredItems.values) {
            selectInternal(item, true)
        }
        updateSelection()
    }

    /** Selects all visible items. Does not fire selection events. */
    fun selectVisible() {
        selectionInfo.reset()
        for (item in visibleItems.values) {
            selectInternal(item, true)
        }
        updateSelection()
    }

    /** Clears the selection for all items. Does not fire selection events. */
    fun clearAllSelection() {
        if (selectionInfo.selection.isNotEmpty()) {
            selectionInfo.reset()
            for (item in filteredItems.values) {
                selectInternal(item, false)
            }
            updateSelection()
        }
    }

    /** Clears the selection for all visible items. Does not fire selection events. */
    fun clearVisibleSelection() {
        if (selectionInfo.selection.isNotEmpty()) {
            selectionInfo.reset()
            for (item in visibleItems.values) {
                selectInternal(item, false)
            }
            updateSelection()
        }
    }

    fun select(item: T, select: Boolean, fireEvent: Boolean = true) {
        selectInternal(item, select)
        if (fireEvent) {
            for (handler in selectHandler) {
                handler(item)
            }
        }
        updateSelection()
    }

    private fun selectInternal(item: T, select: Boolean) {
        if (select) {
            selectionInfo.add(item)
        } else {
            selectionInfo.remove(item)
        }
    }

    private fun updateSelection() {
        for (display in displays) {
            display.updateSelection(selectionInfo)
        }
    }

    // ------------------------------------------------------ filter

    fun addFilter(id: String, filter: Filter<T>) {
        filters[id] = filter
        updateInternal()
    }

    fun removeFilter(id: String) {
        if (id in filters) {
            filters.remove(id)
            updateInternal()
        }
    }

    fun clearFilters() {
        if (filters.isNotEmpty()) {
            filters.clear()
            updateInternal()
        }
    }

    fun hasFilters(): Boolean = filters.isNotEmpty()

    // ------------------------------------------------------ sort

    fun sort(sortInfo: SortInfo<T>) {
        this.sortInfo = sortInfo
        updateInternal()
    }

    // ------------------------------------------------------ paging

    fun pageSize(pageSize: Int) {
        pageInfo.pageSize = pageSize
        updateInternal()
    }

    fun gotoFirstPage() = gotoPage(0)

    fun gotoPreviousPage() {
        if (pageInfo.page > 0) {
            gotoPage(pageInfo.page - 1)
        }
    }

    fun gotoNextPage() {
        if (pageInfo.page < pageInfo.pages - 1) {
            gotoPage(pageInfo.page + 1)
        }
    }

    fun gotoLastPage() = gotoPage(pageInfo.pages - 1)

    fun gotoPage(page: Int) {
        if (page != pageInfo.page) {
            pageInfo.page = page
            updateInternal()
        }
    }
}
