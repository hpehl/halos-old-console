package org.patternfly

import kotlinx.html.Tag

val Tag.aria: Aria
    get() = Aria(this)

class Aria(private val tag: Tag) {

    operator fun set(key: String, value: Any) {
        tag.attributes[failSafeKey(key)] = value.toString()
    }

    private fun failSafeKey(key: String) =
        if (key.startsWith("aria-")) key else "aria-$key"
}
