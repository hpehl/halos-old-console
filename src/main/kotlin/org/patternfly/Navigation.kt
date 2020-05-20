package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import org.patternfly.ComponentType.Navigation
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.events.EventTarget
import kotlin.browser.document
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.plus
import kotlin.collections.set

// ------------------------------------------------------ dsl

fun SidebarTag.pfVerticalNav(block: NavigationTag.() -> Unit = {}) =
    NavigationTag(Orientation.VERTICAL, false, consumer).visit {
        if (this@pfVerticalNav.dark) {
            classes += "dark".modifier()
        }
        block()
    }

fun NavigationTag.pfNavGroup(title: String, block: NavigationGroupTag.() -> Unit) {
    NavigationGroupTag(consumer).visit {
        val titleId = Id.unique("ns")
        attributes["aria-labelledby"] = titleId
        h2("nav".component("section", "title")) {
            id = titleId
            +title
        }
        block()
    }
}

fun NavigationTag.pfNavItems(block: UL.() -> Unit = {}) {
    ul("nav".component("list")) {
        block()
    }
}

fun NavigationGroupTag.pfNavItems(block: UL.() -> Unit = {}) {
    ul("nav".component("list")) {
        block()
    }
}

fun UL.pfNavExpandableItem(title: String, expanded: Boolean = true, block: UL.() -> Unit = {}) {
    li(buildString {
        append("nav".component("item")).append(" ").append("expandable".modifier())
        if (expanded) {
            append(" ").append("expanded".modifier())
        }
    }) {
        a("#", classes = "nav".component("link")) {
            id = Id.unique("nei")
            attributes["aria-expanded"] = expanded.toString()
            onClickFunction = { event ->
                (event.target as Element).let {
                    it.pfNav().toggle(it.id)
                }
            }
            +title
            span("nav".component("toggle")) {
                pfIcon("angle-right".fas())
            }
        }
        section("nav".component("subnav")) {
            hidden = !expanded
            val titleId = Id.unique("ns")
            attributes["aria-labelledby"] = titleId
            h2("pf-screen-reader") {
                id = titleId
                +title
            }
            ul("nav".component("simple-list")) {
                block()
            }
        }
    }
}

fun UL.pfNavItem(item: NavigationItem) {
    li("nav".component("item")) {
        a(item.href, classes = "nav".component("link")) {
            id = item.id
            attributes["data-ni"] = ""
            +item.title
            onClickFunction = { event ->
                (event.target as Element).let {
                    it.pfNav().select(NavigationItem(it.id))
                }
            }
        }
    }
}

// ------------------------------------------------------ tag

class NavigationTag(
    private val orientation: Orientation,
    private val tertiary: Boolean = false,
    consumer: TagConsumer<*>
) : NAV(attributesMapOf("class", "nav".component()), consumer), PatternFlyTag, Aria, Ouia {

    override val componentType: ComponentType = Navigation

    override fun head() {
        if (!tertiary) {
            aria["label"] = "Global"
        }
    }
}

class NavigationGroupTag(consumer: TagConsumer<*>) :
    SECTION(attributesMapOf("class", "nav".component("section")), consumer)

data class NavigationItem(val id: String, val title: String = "", val href: String = "")

enum class Orientation {
    HORIZONTAL, VERTICAL
}

// ------------------------------------------------------ component

fun Document.pfNav(): NavigationComponent {
    val selector = ".${"nav".component()}[aria-label=Global]"
    return (document.querySelector(selector) ?: document.create.nav()).pfNav()
}

fun EventTarget.pfNav(): NavigationComponent = (this as Element).pfNav()

fun Element.pfNav(): NavigationComponent =
    component(this, Navigation, { document.create.nav() }, { it }, ::NavigationComponent)

class NavigationComponent(element: HTMLElement) : PatternFlyComponent<HTMLElement>(element) {
    fun select(item: NavigationItem) {
        console.log("Select navigation item $item")
        val selector = ".${"nav".component("link")}[data-ni]"
        val items = element.querySelectorAll(selector)
        items.asList().map { it as Element }.forEach {
            if (item.id == it.id) {
                it.classList.add("current".modifier())
                it.setAttribute("aria-current", "page")
            } else {
                it.classList.remove("current".modifier())
                it.removeAttribute("aria-current")
            }
        }

        val expandables = element.querySelectorAll(".${"expandable".modifier()}")
        expandables.asList().map { it as Element }.forEach {
            if (it.querySelector("#${item.id}") != null) {
                it.classList.add("current".modifier())
            } else {
                it.classList.remove("current".modifier())
            }
        }
    }

    fun toggle(id: String) {
        console.log("Toggle expandable group $id")
    }
}
