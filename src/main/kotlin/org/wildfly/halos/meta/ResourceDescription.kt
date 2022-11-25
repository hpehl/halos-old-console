package org.wildfly.halos.meta

import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.DESCRIPTION
import org.wildfly.halos.dmr.ModelNode

class ResourceDescription(payload: ModelNode = UNDEFINED, internal val recursive: Boolean = false) : ModelNode() {

    init {
        set(payload)
    }

    val description: String
        get() = get(DESCRIPTION).asString()
}