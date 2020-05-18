package org.patternfly

import kotlinx.html.Tag

val Aria.aria: AriaAccessor
    get() = AriaAccessor(this)

interface Aria : Tag

class AriaAccessor(private val aria: Aria) {
    operator fun set(key: String, value: Any) {
        aria.attributes["aria-$key"] = value.toString()
    }
}
