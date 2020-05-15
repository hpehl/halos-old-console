package org.jboss.dmr

import org.jboss.dmr.ModelDescriptionConstants.Companion.ADDRESS
import org.jboss.dmr.ModelDescriptionConstants.Companion.OP

fun operation(address: ResourceAddress, name: String, block: Operation.() -> Unit): Operation {
    val op = Operation(address, name)
    op.block()
    return op
}

infix fun Operation.params(block: Operation.() -> Unit): Operation {
    block()
    return this
}

class Operation(val address: ResourceAddress, val name: String) : ModelNode() {

    init {
        get(ADDRESS).set(address)
        get(OP).set(name)
    }

    operator fun String.unaryPlus() = param(this to true)
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