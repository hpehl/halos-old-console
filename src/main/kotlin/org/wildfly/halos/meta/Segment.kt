package org.wildfly.halos.meta

sealed class Segment

data class PlaceholderSegment(val placeholder: String) : Segment() {
    override fun toString(): String = "{$placeholder}"
}

data class ValuePlaceholderSegment(val key: String, val placeholder: String) : Segment() {
    override fun toString(): String = "$key={$placeholder}"
}

data class KeyValueSegment(val key: String, val value: String) : Segment() {
    override fun toString(): String = "$key=$value"
}
