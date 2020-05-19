package org.patternfly

import org.patternfly.ComponentType.*
import org.w3c.dom.*

fun <C : PatternFlyComponent<*>> Element.pfComponent(): C? {
    if (this is HTMLElement) {
        val name = dataset["pfc"]
        if (name != null) {
            try {
                // TODO Find a way to get rid of unsafe casts
                val component = when (enumValueOf<ComponentType>(name)) {
                    Alert -> AlertComponent(this.unsafeCast<HTMLDivElement>())
                    Button -> ButtonComponent(this.unsafeCast<HTMLButtonElement>())
                    Content -> ContentComponent(this.unsafeCast<HTMLDivElement>())
                    Icon -> IconComponent(this)
                    Page -> PageComponent(this.unsafeCast<HTMLDivElement>())
                    PageHeader -> PageHeaderComponent(this.unsafeCast<HTMLDivElement>())
                    PageMain -> PageMainComponent(this.unsafeCast<HTMLDivElement>())
                    PageSection -> PageSectionComponent(this.unsafeCast<HTMLDivElement>())
                }
                return component.unsafeCast<C>()
            } catch (e: IllegalArgumentException) {
                console.log("No PatternFly component found for $name")
            }
        }
    }
    return null
}

/** Provides access to the PatternFly component in the DOM */
abstract class PatternFlyComponent<out E : HTMLElement>(val element: E)

enum class ComponentType {
    Alert,
    Button,
    Content,
    Icon,
    Page,
    PageHeader,
    PageMain,
    PageSection
}