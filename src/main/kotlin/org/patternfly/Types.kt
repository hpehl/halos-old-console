package org.patternfly

typealias Identifier<T> = (T) -> String

typealias AsString<T> = (T) -> String

typealias SelectHandler<T> = (T) -> Unit

typealias Filter<T> = (T) -> Boolean
