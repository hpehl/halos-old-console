package org.wildfly.halos.meta

import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.ATTRIBUTES
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.EXECUTE
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.OPERATIONS
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.READ
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.WRITE
import org.wildfly.halos.dmr.ModelNode
import org.wildfly.halos.dmr.Path

class SecurityContext(payload: ModelNode = UNDEFINED, internal val recursive: Boolean = false) : ModelNode() {

    init {
        set(payload)
    }

    /** @return whether the security context is readable */
    fun isReadable(): Boolean = get(READ).asBoolean()

    /** @return whether the security context is writable */
    fun isWritable(): Boolean = get(WRITE).asBoolean()

    /**
     * @param attribute The attribute to check.
     * @return whether the attribute is readable
     */
    fun isReadable(attribute: String): Boolean = lookup(Path(ATTRIBUTES, attribute, READ)).asBoolean()

    /**
     * @param attribute The attribute to check.
     * @return whether the attribute is writable
     */
    fun isWritable(attribute: String): Boolean = lookup(Path(ATTRIBUTES, attribute, WRITE)).asBoolean()

    /**
     * @param operation The operation to check.
     * @return whether the operation is executable
     */
    fun isExecutable(operation: String): Boolean = lookup(Path(OPERATIONS, operation, EXECUTE)).asBoolean()
}