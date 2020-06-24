package org.jboss.dmr

import org.jboss.dmr.ModelDescriptionConstants.Companion.NAME
import kotlin.js.Date

/** A model node with a name. */
open class NamedNode(name: String, node: ModelNode) : ModelNode() {

    var name: String
        get() = get(NAME).asString()
        set(value) {
            get(NAME).set(value)
        }

    var node: ModelNode = node
        set(value) {
            val olName = get(NAME)
            set(value)
            get(NAME).set(olName)
        }

    init {
        set(node)
        get(NAME).set(name)
    }

    constructor(node: ModelNode) : this(
        if (NAME in node)
            node[NAME].asString()
        else
            "${ModelDescriptionConstants.UNDEFINED}-${Date().getMilliseconds()}",
        node
    )

    constructor(property: Property) : this(property.name, property.value)
}
