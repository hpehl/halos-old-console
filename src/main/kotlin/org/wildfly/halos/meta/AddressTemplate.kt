package org.wildfly.halos.meta

import org.wildfly.halos.dmr.ModelNode
import org.wildfly.halos.dmr.ResourceAddress

class AddressTemplate private constructor(private val segments: List<Segment>) : Iterable<Segment> {

    constructor(template: String) : this(parse(template))

    val template: String
        get() = segments.joinToString("/") { it.toString() }

    val size: Int
        get() = segments.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as AddressTemplate

        if (segments != other.segments) return false

        return true
    }

    override fun hashCode(): Int {
        return segments.hashCode()
    }

    override fun toString(): String = template

    fun isEmpty(): Boolean = segments.isEmpty()

    fun first(): Segment? = segments.firstOrNull()

    fun last(): Segment? = segments.lastOrNull()

    fun parent(): AddressTemplate = if (isEmpty()) this else {
        AddressTemplate(segments.dropLast(1))
    }

    override fun iterator(): Iterator<Segment> = segments.iterator()

    operator fun plus(template: String): AddressTemplate = AddressTemplate(segments + parse(template))

    operator fun plus(template: AddressTemplate): AddressTemplate = AddressTemplate(segments + template.segments)

    fun resolve(resolver: SegmentResolver): ResourceAddress =
        if (isEmpty()) {
            ResourceAddress.root()
        } else {
            val node = ModelNode()
            segments.forEachIndexed { index, segment ->
                val resolved = resolver.resolve(segment, index, index == 0, index == segments.size - 1)
                if (resolved is KeyValueSegment) {
                    node.add(resolved.key, resolved.value)
                } else {
                    throw ResolveException("Segment '$resolved' of template '$this' could not be resolved to a <key>=<value> segment!")
                }
            }
            ResourceAddress(node)
        }

    companion object {
        val ROOT: AddressTemplate = AddressTemplate(emptyList())

        private fun parse(template: String): List<Segment> = template
            .split('/')
            .filter { it.isNotEmpty() }
            .map { segment ->
                if (segment.contains('=')) {
                    val (key, value) = segment.split('=').let {
                        Pair(it[0], it.getOrElse(1) { "" })
                    }
                    if (value.isPlaceholder()) {
                        ValuePlaceholderSegment(key, value.extractPlaceholder())
                    } else {
                        KeyValueSegment(key, value)
                    }
                } else {
                    if (!segment.isPlaceholder()) {
                        throw IllegalArgumentException("Cannot parse '$template': '$segment' is not a valid segment!")
                    }
                    PlaceholderSegment(segment.extractPlaceholder())
                }
            }

        private fun String.isPlaceholder() = startsWith('{') && endsWith('}')
        private fun String.extractPlaceholder() = this.trimStart('{').trimEnd('}')
    }
}
