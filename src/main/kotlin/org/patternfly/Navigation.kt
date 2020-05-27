package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import org.jboss.elemento.aria
import org.jboss.elemento.hidden
import org.jboss.elemento.minusAssign
import org.jboss.elemento.plusAssign
import org.patternfly.ComponentType.Navigation
import org.w3c.dom.*
import org.w3c.dom.events.EventTarget
import kotlin.browser.document
import kotlin.browser.window
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
        aria["labelledby"] = titleId
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

fun UL.pfNavExpandableGroup(title: String, expanded: Boolean = false, block: UL.() -> Unit = {}) {
    li(buildString {
        append("nav".component("item")).append(" ").append("expandable".modifier())
        if (expanded) {
            append(" ").append("expanded".modifier())
        }
    }) {
        a("#", classes = "nav".component("link")) {
            aria["expanded"] = expanded.toString()
            onClickFunction = {
                with(it.target as Element) {
                    pfNav().toggle(this)
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
            aria["labelledby"] = titleId
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
        a(item.url, classes = "nav".component("link")) {
            id = item.id
            attributes[Dataset.NAVIGATION_ITEM.long] = "" // marker for navigation items
            +item.title
            onClickFunction = {
                with(it.target as Element) {
                    pfNav().select(item)
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
) : NAV(attributesMapOf("class", "nav".component()), consumer), PatternFlyTag, Ouia {

    override val componentType: ComponentType = Navigation

    override fun head() {
        if (!tertiary) {
            aria["label"] = "Global"
        }
    }
}

class NavigationGroupTag(consumer: TagConsumer<*>) :
    SECTION(attributesMapOf("class", "nav".component("section")), consumer)

data class NavigationItem(val url: String, val title: String = "") {
    internal val id: String = Id.build("ni", url)
}

enum class Orientation {
    HORIZONTAL, VERTICAL
}

// ------------------------------------------------------ component

private val globalNav: NavigationComponent by lazy {
    val selector = ".${"nav".component()}[aria-label=Global]"
    document.querySelector(selector).pfNav()
}

fun Document.pfNav(): NavigationComponent = globalNav

fun EventTarget?.pfNav(): NavigationComponent = (this as Element).pfNav()

fun Element?.pfNav(): NavigationComponent =
    component(this, Navigation, { document.create.nav() }, { it }, ::NavigationComponent)

class NavigationComponent(element: HTMLElement) : PatternFlyComponent<HTMLElement>(element) {

    fun autoSelect(hashToNavigationItem: (String) -> NavigationItem) {
        window.addEventListener("popstate", {
            val navigationItem = hashToNavigationItem((it.target as Window).location.hash)
            document.pfNav().select(navigationItem)
        })
    }

    fun select(item: NavigationItem) {
        // first (de)select the items
        val selector = ".${"nav".component("link")}[${Dataset.NAVIGATION_ITEM.long}]"
        val items = element.querySelectorAll(selector)
        items.asList().map { it as Element }.forEach {
            if (item.id == it.id) {
                it.classList += "current".modifier()
                it.aria["current"] = "page"
            } else {
                it.classList -= "current".modifier()
                it.aria.remove("current")
            }
        }

        // then (de)select the expandable parents (if any)
        val expandables = element.querySelectorAll(".${"expandable".modifier()}")
        expandables.asList().map { it as Element }.forEach {
            // it = li.pf-c-nav__item.pf-m-expandable
            if (it.querySelector("#${item.id}") != null) {
                it.classList += "current".modifier()
                it.firstElementChild?.let { a -> expand(a) }
            } else {
                it.classList -= "current".modifier()
            }
        }
    }

    internal fun toggle(element: Element) {
        if (element.aria["expanded"].toBoolean()) {
            collapse(element)
        } else {
            expand(element)
        }
    }

    private fun collapse(element: Element) {
        val li = element.parentElement
        val section = element.nextElementSibling
        if (li != null && section != null) {
            li.classList -= "expanded".modifier()
            element.aria["expanded"] = false
            section.hidden = true
        }
    }

    private fun expand(element: Element) {
        val li = element.parentElement
        val section = element.nextElementSibling
        if (li != null && section != null) {
            li.classList += "expanded".modifier()
            element.aria["expanded"] = true
            section.removeAttribute("hidden")
        }
    }
}
