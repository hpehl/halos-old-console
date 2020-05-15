package org.jboss.dmr

infix fun String.op(operation: String): Operation = Operation(ResourceAddress.from(this), operation)

infix fun ResourceAddress.op(operation: String): Operation = Operation(this, operation)

fun String.adr(): ResourceAddress = ResourceAddress.from(this)

fun address(block: ResourceAddress.() -> Unit): ResourceAddress {
    val a = ResourceAddress()
    a.block()
    return a
}

class ResourceAddress() : ModelNode() {

    init {
        setEmptyList()
    }

    private constructor(address: List<ModelNode>) : this() {
        set(address)
    }

    operator fun Pair<String, String>.unaryPlus() = add(this)

    fun add(segment: Pair<String, String>) {
        add().set(segment.first, segment.second)
    }

    fun parent(): ResourceAddress = if (this == root()) {
        this
    } else {
        ResourceAddress(asList().dropLast(1))
    }

    fun isEmpty(): Boolean = asList().isEmpty()
    fun isNotEmpty(): Boolean = asList().isNotEmpty()

    override fun toString(): String = if (defined() && isNotEmpty()) {
        "/" + asPropertyList().joinToString("/") { "${it.name}=${it.value.asString()}" }
    } else {
        ""
    }

    companion object {
        fun root(): ResourceAddress =
            ResourceAddress()

        fun from(address: String): ResourceAddress {
            val ra = ResourceAddress()
            val safeAddress = if (address.startsWith("/")) address.substring(1) else address
            for (segment in safeAddress.split("/")) {
                val parts = segment.split("=")
                if (parts.size == 2) {
                    ra.add(parts[0], parts[1])
                }
            }
            return ra
        }
    }
}