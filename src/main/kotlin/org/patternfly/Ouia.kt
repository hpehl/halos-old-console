package org.patternfly

import org.patternfly.Dataset.OUIA_COMPONENT_TYPE
import org.w3c.dom.get
import kotlin.browser.window

internal fun setOuiaType(tag: PatternFlyTag) {
    if (isSupported()) tag.attributes[OUIA_COMPONENT_TYPE.long] = tag.componentType.name
}

private fun isSupported(): Boolean = window.localStorage.get("ouia")?.toBoolean() == true

internal interface Ouia
