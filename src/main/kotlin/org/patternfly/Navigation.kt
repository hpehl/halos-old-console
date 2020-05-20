package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import org.patternfly.ComponentType.Navigation
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.browser.document

// ------------------------------------------------------ dsl

fun SectioningOrFlowContent.pfVerticalNav(mode: Mode = Mode.SIMPLE, block: NavigationTag.() -> Unit = {}) =
    NavigationTag(Orientation.VERTICAL, mode, false, consumer).visit(block)

// ------------------------------------------------------ tag

class NavigationTag(
    private val orientation: Orientation,
    private val mode: Mode,
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

data class NavigationItem(val id: String, val title: String, val href: String)

enum class Orientation {
    HORIZONTAL, VERTICAL
}

enum class Mode {
    SIMPLE, GROUPED, EXPANDABLE
}

// ------------------------------------------------------ component

fun Element.pfNav(): NavigationComponent =
    component(this, Navigation, { document.create.nav() }, { it }, ::NavigationComponent)

class NavigationComponent(element: HTMLElement) : PatternFlyComponent<HTMLElement>(element)
