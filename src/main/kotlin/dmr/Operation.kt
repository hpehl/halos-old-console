package org.wildfly.halos.dmr

import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.ADDRESS
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.OP

fun operation(address: ResourceAddress, name: String, block: Operation.() -> Unit): Operation {
    val op = Operation(address, name)
    op.block()
    return op
}

class Operation(val address: ResourceAddress, val name: String) : ModelNode() {

    init {
        get(OP).set(name)
        get(ADDRESS).set(address)
    }

    fun param(name: String, value: Any) = get(name).set(value)

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(address).append(":").append(name)
        if (size > 2) { // TODO Not the best way to detect parameters
            builder.append("(")
            asPropertyList()
                .filter { it.name != OP && it.name != ADDRESS }
                .joinTo(builder, ",") { "${it.name}=${it.value.asString()}" }
            builder.append(")")
        }
        return builder.toString()
    }
}