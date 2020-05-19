package org.patternfly

import org.w3c.dom.*
import kotlin.browser.document

fun foo() {
    val alert = document.getElementById("foo")!!.pfComponent<AlertComponent>()
}

fun <C : PatternFlyComponent<*>> Element.pfComponent(): C? {
    if (this is HTMLElement) {
        val name = dataset["pfc"]
        if (name != null) {
            try {
                val c = enumValueOf<ComponentType>(name)
                val component = when (c) {
                    ComponentType.Alert -> AlertComponent(this.unsafeCast<HTMLDivElement>())
                    ComponentType.Button -> ButtonComponent(this.unsafeCast<HTMLButtonElement>())
                    ComponentType.Content -> ContentComponent(this.unsafeCast<HTMLDivElement>())
                    ComponentType.Icon -> IconComponent(this)
                    ComponentType.Page -> PageComponent(this.unsafeCast<HTMLDivElement>())
                    ComponentType.PageHeader -> PageHeaderComponent(this.unsafeCast<HTMLDivElement>())
                    ComponentType.PageMain -> PageMainComponent(this.unsafeCast<HTMLDivElement>())
                    ComponentType.PageSection -> PageSectionComponent(this.unsafeCast<HTMLDivElement>())
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