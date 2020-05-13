package org.wildfly.halos.dmr

class ResourceAddress() : ModelNode() {

    init {
        setEmptyList()
    }

    constructor(address: ModelNode) : this() {
        set(address)
    }

    constructor(address: List<ModelNode>) : this() {
        set(address)
    }

    fun add(name: String, value: String) {
        super.add(name, value)
    }

    fun add(address: ResourceAddress) {
        for (segment in address.asPropertyList()) {
            add(segment.name, segment.value)
        }
    }

    fun parent(): ResourceAddress = if (this == root()) {
        this
    } else {
        ResourceAddress(asList().dropLast(1))
    }

    fun isEmpty(): Boolean = asList().isEmpty()
    fun isNotEmpty(): Boolean = asList().isNotEmpty()

    override fun toString(): String = if (defined && isNotEmpty()) {
        "/" + asPropertyList().joinToString("/") { "${it.name}=${it.value.asString()}" }
    } else {
        ""
    }

    companion object {
        fun root(): ResourceAddress = ResourceAddress()

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