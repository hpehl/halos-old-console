package org.patternfly

fun component(component: String, vararg elements: String): String = buildString {
    append("pf-c-")
    append(component)
    if (elements.isNotEmpty()) {
        elements.joinTo(this, "-", "__")
    }
}

fun modifier(modifier: String) = "pf-m-$modifier"