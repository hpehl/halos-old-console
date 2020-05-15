package org.jboss.dmr

import kotlinx.html.currentTimeMillis
import org.jboss.dmr.ModelDescriptionConstants.Companion.NAME

/** A model node with a name. */
class NamedNode(name: String, node: ModelNode) : ModelNode() {

    var node: ModelNode = node
        set(value) {
            val name = get(NAME)
            set(value)
            get(NAME).set(name)
        }

    init {
        set(node)
        get(NAME).set(name)
    }

    constructor(node: ModelNode) : this(
        if (NAME in node)
            node[NAME].asString()
        else
            "${ModelDescriptionConstants.UNDEFINED}_${currentTimeMillis()}",
        node
    )

    constructor(property: Property) : this(property.name, property.value)
}