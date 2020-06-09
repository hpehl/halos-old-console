package org.patternfly

import kotlinx.html.A
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.li
import kotlinx.html.role
import kotlinx.html.span
import kotlinx.html.ul
import kotlinx.html.visitAndFinalize
import org.jboss.elemento.Id
import org.patternfly.ComponentType.Dropdown
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ api

typealias DropdownRenderer<T> = (T) -> DropdownItemTag.() -> Unit

private val ddr: MutableMap<String, DropdownRenderer<*>> = mutableMapOf()

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun <T> FlowContent.pfDropdown(text: String, rightAlign: Boolean = false, block: DropdownTag<T>.() -> Unit = {}) =
    DropdownTag<T>(text, null, rightAlign, consumer).visitPf(block)

@HtmlTagMarker
fun <T> FlowContent.pfIconDropdown(
    iconClass: String,
    rightAlign: Boolean = false,
    block: DropdownTag<T>.() -> Unit = {}
) =
    DropdownTag<T>(null, iconClass, rightAlign, consumer).visitPf(block)

@HtmlTagMarker
private fun <T, C : TagConsumer<T>> C.pfDropdownItem(block: DropdownItemTag.() -> Unit = {}): T =
    DropdownItemTag(this).visitAndFinalize(this, block)

// ------------------------------------------------------ tag

class DropdownTag<T>(
    private val text: String?,
    private val iconClass: String?,
    private val rightAlign: Boolean,
    consumer: TagConsumer<*>
) :
    DIV(attributesMapOf("class", "dropdown".component()), consumer),
    PatternFlyTag, Ouia {

    private val id: String = Id.unique()
    override val componentType: ComponentType = Dropdown

    var identifier: Identifier<T>? = null
        set(value) {
            field = value
            if (value != null) {
                attributes[Dataset.REGISTRY.long] = id
                identifierRegistry[id] = value
            }
        }

    var asString: AsString<T>? = null
        set(value) {
            field = value
            if (value != null) {
                attributes[Dataset.REGISTRY.long] = id
                asStringRegistry[id] = value
            }
        }

    var renderer: DropdownRenderer<T>? = null
        set(value) {
            field = value
            if (value != null) {
                attributes[Dataset.REGISTRY.long] = id
                ddr[id] = value
            }
        }

    var onSelect: SelectHandler<T>? = null
        set(value) {
            field = value
            if (value != null) {
                attributes[Dataset.REGISTRY.long] = id
                selectRegistry[id] = value
            }
        }

    override fun head() {
        val buttonId = Id.unique(Dropdown.name)
        button(classes = "dropdown".component("toggle")) {
            id = buttonId
            aria["expanded"] = false
            aria["haspopup"] = true
            onClickFunction = { it.target.pfDropdown<T>().ceh.expand() }
            when {
                this@DropdownTag.text != null -> {
                    span("dropdown".component("toggle", "text")) { +this@DropdownTag.text }
                    pfIcon("caret-down".fas()) {
                        classes += "dropdown".component("toggle", "icon")
                    }
                }
                this@DropdownTag.iconClass != null -> {
                    classes += "plain".modifier()
                    pfIcon(this@DropdownTag.iconClass)
                }
            }
        }
        ul("dropdown".component("menu")) {
            hidden = true
            role = "menu"
            aria["labelledby"] = buttonId
            if (this@DropdownTag.rightAlign) {
                classes += "align-right".modifier()
            }
        }
    }
}

class DropdownItemTag(consumer: TagConsumer<*>) :
    A(attributesMapOf("class", "dropdown".component("menu-item"), "tabindex", "-1"), consumer)

// ------------------------------------------------------ component

fun <T> EventTarget?.pfDropdown(): DropdownComponent<T> = (this as Element).pfDropdown()

fun <T> Element?.pfDropdown(): DropdownComponent<T> =
    component(this, Dropdown, { document.create.div() }, { it as HTMLDivElement }, ::DropdownComponent)

class DropdownComponent<T>(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {

    private val button = element.querySelector(".${"dropdown".component("toggle")}")
    private val menu = element.querySelector(".${"dropdown".component("menu")}")
    internal val ceh: CollapseExpandHandler = CollapseExpandHandler(element, button as HTMLElement, menu as HTMLElement)
    private val identifier: Identifier<T> by identifier<DropdownComponent<T>, T>()
    private val asString: AsString<T> by asString<DropdownComponent<T>, T>()
    private val onSelect: SelectHandler<T>? by selectHandler<DropdownComponent<T>, T>()
    private val renderer: DropdownRenderer<T> by RegistryLookup<DropdownComponent<T>, DropdownRenderer<T>>(ddr) {
        {
            {
                +asString(it)
            }
        }
    }

    fun addAll(items: List<T>) {
        for (item in items) {
            add(item)
        }
    }

    fun add(item: T) {
        menu?.let {
            it.append {
                li {
                    role = "menuitem"
                    pfDropdownItem {
                        attributes[Dataset.DROPDOWN_ITEM.long] = identifier(item)
                        onClickFunction = {
                            ceh.collapse()
                            this@DropdownComponent.onSelect?.let {
                                it(item)
                            }
                        }
                        val block = renderer(item)
                        block(this)
                    }
                }
            }
        }
    }
}
