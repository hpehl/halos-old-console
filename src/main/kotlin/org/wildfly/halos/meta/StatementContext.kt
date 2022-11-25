package org.wildfly.halos.meta

object StatementContext {

    private val placeholders: MutableMap<String, Placeholder> = mutableMapOf()
    private val values: MutableMap<Placeholder, String> = mutableMapOf()

    fun assign(placeholder: String, value: String, resource: String? = null) {
        val ph = placeholders.getOrPut(placeholder) { Placeholder(placeholder, resource) }
        values[ph] = value
    }

    fun resolve(segment: Segment): Segment = when (segment) {
        is PlaceholderSegment -> {
            val placeholder = failSafePlaceholder(segment.placeholder)
            if (placeholder.resource != null) {
                KeyValueSegment(placeholder.resource, failSafeValue(placeholder))
            } else {
                throw ResolveException("No resource bound to $placeholder")
            }
        }
        is ValuePlaceholderSegment -> {
            val placeholder = failSafePlaceholder(segment.placeholder)
            val value = failSafeValue(placeholder)
            KeyValueSegment(segment.key, value)
        }
        is KeyValueSegment -> segment
    }

    private fun failSafePlaceholder(name: String) =
        placeholders[name] ?: throw ResolveException("Placeholder not registered: $name")

    private fun failSafeValue(placeholder: Placeholder) =
        values[placeholder] ?: throw ResolveException("No value found for $placeholder")
}

class ResolveException(message: String) : Throwable(message)

private data class Placeholder(val name: String, val resource: String?) {
    override fun toString(): String = "{$name}"
}
