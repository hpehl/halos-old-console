package org.patternfly

typealias Identifier<T> = (T) -> String

typealias AsString<T> = (T) -> String

typealias SelectHandler<T> = (T) -> Unit

typealias Filter<T> = (T) -> Boolean

enum class Direction {
    RIGHT, UP
}

enum class Orientation {
    HORIZONTAL, VERTICAL
}

enum class Size(val modifier: String) {
    _4xl("4xl".modifier()),
    _3xl("3xl".modifier()),
    _2xl("2xl".modifier()),
    xl("xl".modifier()),
    lg("lg".modifier()),
    md("md".modifier())
}