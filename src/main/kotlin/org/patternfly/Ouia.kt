package org.patternfly

import org.w3c.dom.get
import kotlin.browser.window

internal fun setOuiaType(tag: PatternFlyTag) {
    if (isSupported()) tag.attributes["data-ouia-component-type"] = tag.componentType.name
}

private fun isSupported(): Boolean = window.localStorage.get("ouia")?.toBoolean() == true

internal interface Ouia