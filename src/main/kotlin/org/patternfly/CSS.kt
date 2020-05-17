package org.patternfly

fun String.component(vararg elements: String): String = buildString {
    append("pf-c-${this@component}")
    if (elements.isNotEmpty()) elements.joinTo(this, "-", "__")
}

fun String.modifier(): String = "pf-m-$this"

fun String.fas() = "fas fa-$this"