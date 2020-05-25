package org.jboss.elemento

import org.w3c.dom.DOMTokenList
import org.w3c.dom.Element
import org.w3c.dom.Node

// ------------------------------------------------------ token list

operator fun DOMTokenList.plusAssign(value: String) {
    this.add(value)
}

operator fun DOMTokenList.minusAssign(value: String) {
    this.remove(value)
}

// ------------------------------------------------------ node

fun Node?.removeFromParent() {
    if (this != null && this.parentNode != null) {
        this.parentNode!!.removeChild(this)
    }
}

// ------------------------------------------------------ element

val Element.aria: Aria
    get() = Aria(this)

var Element.hidden
    get() = getAttribute("hidden")?.toBoolean()
    set(value) {
        setAttribute("hidden", value.toString())
    }

class Aria(private val element: Element) {

    operator fun contains(name: String): Boolean = element.hasAttribute(name)

    operator fun get(name: String): String = element.getAttribute(failSafeKey(name)) ?: ""

    operator fun set(name: String, value: Any) {
        element.setAttribute(failSafeKey(name), value.toString())
    }

    fun remove(name: String) {
        element.removeAttribute(failSafeKey(name))
    }

    private fun failSafeKey(name: String) =
        if (name.startsWith("aria-")) name else "aria-$name"
}
