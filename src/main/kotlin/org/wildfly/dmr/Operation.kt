package org.wildfly.dmr

import org.wildfly.dmr.ModelDescriptionConstants.Companion.ADDRESS
import org.wildfly.dmr.ModelDescriptionConstants.Companion.OP

fun operation(address: ResourceAddress, name: String, block: Operation.() -> Unit): Operation {
    val op = Operation(address, name)
    op.block()
    return op
}

class Operation(val address: ResourceAddress, val name: String) : ModelNode() {

    init {
        get(ADDRESS).set(address)
        get(OP).set(name)
    }

    operator fun Pair<String, Any>.unaryPlus() = param(this)

    fun param(p: Pair<String, Any>) = get(p.first).set(p.second)

    override fun toString(): String = buildString {
        append(address).append(":").append(name)
        if (size() > 2) { // TODO Not the best way to detect parameters
            append("(")
            asPropertyList()
                .filter { it.name != OP && it.name != ADDRESS }
                .joinTo(this, ",") { "${it.name}=${it.value.asString()}" }
            append(")")
        }
    }
}