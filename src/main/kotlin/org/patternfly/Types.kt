package org.patternfly

import kotlinx.html.FlowContent

typealias Identifier<T> = (T) -> String

typealias AsString<T> = (T) -> String

typealias Renderer<T> = (T) -> FlowContent.() -> Unit

typealias SelectHandler<T> = (T) -> Unit

typealias Filter<T> = (T) -> Boolean
