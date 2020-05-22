package org.patternfly

import org.patternfly.Data.COMPONENT_TYPE
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

internal fun <C : PatternFlyComponent<E>, E : HTMLElement> component(
    element: Element,
    componentType: ComponentType,
    defaultElement: () -> E,
    targetElement: (element: HTMLElement) -> E,
    create: (element: E) -> C
): C {
    if (element is HTMLElement) {
        val id = element.dataset["pfc"]
        if (componentType.id == id) {
            return create(targetElement(element))
        } else {
            val closest = element.closest("[$COMPONENT_TYPE=${componentType.id}]")
            if (closest != null) {
                return create(targetElement(closest as HTMLElement))
            }
        }
    }
    return create(defaultElement())
}

/** Provides access to the PatternFly component in the DOM */
abstract class PatternFlyComponent<out E : HTMLElement>(val element: E)

enum class ComponentType(val id: String) {
    Alert("alrt"),
    Button("btn"),
    Content("cnt"),
    DataList("dl"),
    Icon("i"),
    Navigation("nav"),
    Page("pg"),
    PageHeader("pgh"),
    PageMain("pgm"),
    PageSection("pgs"),
    Sidebar("sb")
}