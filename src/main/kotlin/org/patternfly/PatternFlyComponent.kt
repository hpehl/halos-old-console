package org.patternfly

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

fun ComponentType.selector(): String = "[${Dataset.COMPONENT_TYPE.long}=${id}]"

internal fun <C : PatternFlyComponent<E>, E : HTMLElement> component(
    element: Element?,
    componentType: ComponentType,
    defaultElement: () -> E,
    targetElement: (element: HTMLElement) -> E,
    create: (element: E) -> C
): C {
    if (element is HTMLElement) {
        val id = element.dataset[Dataset.COMPONENT_TYPE.short]
        return if (componentType.id == id) {
            create(targetElement(element))
        } else {
            val closest = element.closest(componentType.selector())
            if (closest != null) {
                create(targetElement(closest as HTMLElement))
            } else {
                console.error("Unable to find element for $componentType")
                create(defaultElement())
            }
        }
    } else {
        console.error("Unable to find element for $componentType")
        return create(defaultElement())
    }
}

/** Provides access to the PatternFly component in the DOM */
abstract class PatternFlyComponent<out E : HTMLElement>(val element: E)
