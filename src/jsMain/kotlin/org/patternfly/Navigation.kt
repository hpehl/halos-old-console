package org.patternfly

import dev.fritz2.binding.handledBy
import dev.fritz2.dom.Tag
import dev.fritz2.dom.html.HtmlElements
import dev.fritz2.lenses.WithId
import dev.fritz2.routing.Router
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLUListElement

// ------------------------------------------------------ dsl

fun <T : WithId> HtmlElements.pfNavigation(
    router: Router<T>,
    orientation: Orientation,
    tertiary: Boolean = false,
    content: Navigation<T>.() -> Unit = {}
): Navigation<T> = register(Navigation(router, orientation, tertiary), content)

fun <T : WithId> Navigation<T>.pfNavItems(content: NavigationItems<T>.() -> Unit = {}): NavigationItems<T> =
    register(NavigationItems(this), content)

fun <T : WithId> NavigationItems<T>.pfNavigationItem(text: String, item: T) {
    register(
        li(baseClass = "nav".component("item")) {
            a(baseClass = "nav".component("link")) {
                text(text)
                val navigation = this@pfNavigationItem.navigation
                clicks.map { item } handledBy navigation.router.navTo
                classMap = navigation.router.routes.map {
                    mapOf("current".modifier() to (it.id == item.id))
                }
                // TODO set aria-current
            }
        }, {})
}

// ------------------------------------------------------ tag

class Navigation<T>(
    internal val router: Router<T>,
    private val orientation: Orientation,
    private val tertiary: Boolean
) : PatternFlyTag<HTMLElement>(ComponentType.Navigation, "nav", "nav".component()), Ouia {

    init {
        if (!tertiary) {
            attr("aria-label", "Global")
        }
    }
}

class NavigationItems<T>(internal val navigation: Navigation<T>) :
    Tag<HTMLUListElement>("ul", baseClass = "nav".component("list"))
