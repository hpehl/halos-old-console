package org.patternfly

import org.jboss.elemento.aria
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import kotlin.browser.document

internal class CollapseExpandHandler(
    private val root: HTMLElement,
    private val button: HTMLElement,
    private val menu: HTMLElement
) {

    private val closeHandler: (Event) -> Unit = {
        val clickInside = root.contains(it.target as Node)
        if (!clickInside) {
            collapse()
        }
    }

    internal fun expand() {
        if (expanded()) {
            collapse()
        } else {
            document.addEventListener("click", closeHandler)
            root.classList.add("expanded".modifier())
            button.aria["expanded"] = true
            menu.hidden = false
        }
    }

    internal fun collapse() {
        if (expanded()) {
            root.classList.remove("expanded".modifier())
            button.aria["expanded"] = false
            menu.hidden = true
            document.removeEventListener("click", closeHandler)
        }
    }

    private fun expanded(): Boolean = root.classList.contains("expanded".modifier())
}