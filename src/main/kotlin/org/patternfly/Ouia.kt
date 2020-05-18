package org.patternfly

import kotlinx.html.Tag
import org.w3c.dom.get
import kotlin.browser.window

fun Ouia.ouiaComponent(component: String) {
    if (isSupported()) attributes["data-ouia-component-type"] = component
}

fun Ouia.ouiaId(id: String) {
    if (isSupported()) attributes["data-ouia-component-id"] = id
}

private fun isSupported(): Boolean = window.localStorage.get("ouia")?.toBoolean() == true

interface Ouia : Tag
