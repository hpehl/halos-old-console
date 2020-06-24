package org.patternfly

import dev.fritz2.binding.RootStore
import dev.fritz2.binding.const
import dev.fritz2.binding.each
import dev.fritz2.binding.handledBy
import dev.fritz2.dom.html.A
import dev.fritz2.dom.html.HtmlElements
import dev.fritz2.dom.html.render
import dev.fritz2.identification.uniqueId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import org.jboss.elemento.Id
import org.patternfly.Dataset.DROPDOWN_ITEM
import org.w3c.dom.HTMLDivElement

// ------------------------------------------------------ dsl

typealias DropdownDisplay<T> = (T) -> A.() -> Unit

fun <T> HtmlElements.pfDropdown(
    text: String,
    store: DropdownStore<T>,
    content: Dropdown<T>.() -> Unit = {}
): Dropdown<T> = register(Dropdown(text, store), content)

// ------------------------------------------------------ tag

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class Dropdown<T> internal constructor(private val text: String, private val store: DropdownStore<T>) :
    PatternFlyTag<HTMLDivElement>(ComponentType.Dropdown, "div", "dropdown".component()), Ouia {

    private val ces = CollapseExpanseStore(domNode)
    var identifier: Identifier<T> = { Id.asId(it.toString()) }
    var asText: AsText<T> = { it.toString() }
    var display: DropdownDisplay<T> = {
        {
            text(this@Dropdown.asText.invoke(it))
        }
    }

    init {
        classMap = ces.data.map { expanded -> mapOf("expanded".modifier() to expanded) }
        val buttonId = uniqueId()
        button(id = buttonId, baseClass = "dropdown".component("toggle")) {
            attr("aria-haspopup", true.toString())
            this@Dropdown.ces.data.bindAttr("aria-expanded", domNode)
            clicks handledBy this@Dropdown.ces.expand
            span(baseClass = "dropdown".component("toggle", "text")) {
                text(this@Dropdown.text)
            }
            span(baseClass = "dropdown".component("toggle", "icon")) {
                pfIcon("caret-down".fas()) {
                    classList = const(listOf("dropdown".component("toggle", "icon")))
                }
            }
        }
        ul(baseClass = "dropdown".component("menu")) {
            attr("role", "menu")
            attr("aria-labelledby", buttonId)
            this@Dropdown.ces.data.map { (!it).toString() }.bindAttr("hidden")
            this@Dropdown.store.data.each().map { item ->
                render {
                    li {
                        attr("role", "menuitem")
                        a(baseClass = "dropdown".component("menu-item")) {
                            attr("tabindex", "-1")
                            attr(DROPDOWN_ITEM.long, this@Dropdown.identifier.invoke(item))
                            clicks.map { item } handledBy this@Dropdown.store.selects
                            clicks handledBy this@Dropdown.ces.collapse
                            val content = this@Dropdown.display.invoke(item)
                            content.invoke(this)
                        }
                    }
                }
            }.bind()
        }
    }
}

// ------------------------------------------------------ store

class DropdownStore<T> : RootStore<List<T>>(listOf()) {
    val selects = handleAndEmit<T, T> { items, item ->
        offer(item)
        items
    }
}
