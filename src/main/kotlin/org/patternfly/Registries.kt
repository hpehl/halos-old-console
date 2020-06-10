package org.patternfly

import org.jboss.elemento.Id
import org.patternfly.Dataset.REGISTRY
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal val identifierRegistry: MutableMap<String, Identifier<*>> = mutableMapOf()
internal val asStringRegistry: MutableMap<String, AsString<*>> = mutableMapOf()
internal val selectRegistry: MutableMap<String, SelectHandler<*>> = mutableMapOf()

internal fun <R : PatternFlyComponent<HTMLElement>, T> identifier(): RegistryLookup<R, Identifier<T>> =
    RegistryLookup(identifierRegistry) {
        { item: T -> Id.asId(item.toString()) }
    }

internal fun <R : PatternFlyComponent<HTMLElement>, T> asString(): RegistryLookup<R, AsString<T>> =
    RegistryLookup(asStringRegistry) {
        { item: T -> item.toString() }
    }

internal fun <R : PatternFlyComponent<HTMLElement>, T> selectHandler(): NullableRegistryLookup<R, SelectHandler<T>?> =
    NullableRegistryLookup(selectRegistry)

internal class RegistryLookup<in R : PatternFlyComponent<HTMLElement>, out T>(
    private val registry: Map<String, *>,
    private val default: () -> T
) : ReadOnlyProperty<R, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        val id = thisRef.element.dataset[REGISTRY.short]
        return if (id != null) {
            registry.getOrElse(id, default) as T
        } else {
            default()
        }
    }
}

internal class NullableRegistryLookup<in R : PatternFlyComponent<HTMLElement>, out T>(
    private val registry: Map<String, *>
) : ReadOnlyProperty<R, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: R, property: KProperty<*>): T? {
        val id = thisRef.element.dataset[REGISTRY.short]
        return if (id != null) {
            registry[id] as T
        } else {
            null
        }
    }
}
