package org.patternfly

import kotlinx.html.Tag

//interface Aria : Tag {
//    val aria: AriaAccessor(this)
//}

class AriaAccessor(private val tag: Tag) {
    operator fun set(key: String, value: String) = tag.attributes.put("aria-$key", value)
}
