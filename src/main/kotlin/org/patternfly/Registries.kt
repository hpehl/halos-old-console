package org.patternfly

import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal val identifierRegistry: MutableMap<String, Identifier<*>> = mutableMapOf()
internal val asStringRegistry: MutableMap<String, AsString<*>> = mutableMapOf()

internal fun <R : PatternFlyComponent<HTMLElement>, T> identifier(): RegistryLookup<R, Identifier<T>> =
    RegistryLookup(Dataset.REGISTRY, identifierRegistry) {
        { item: T -> Id.asId(item.toString()) }
    }

internal fun <R : PatternFlyComponent<HTMLElement>, T> asString(): RegistryLookup<R, AsString<T>> =
    RegistryLookup(Dataset.REGISTRY, asStringRegistry) {
        { item: T -> item.toString() }
    }

internal class RegistryLookup<in R : PatternFlyComponent<HTMLElement>, out T>(
    private val name: Dataset,
    private val registry: Map<String, *>,
    private val default: () -> T
) : ReadOnlyProperty<R, T> {

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        val id = thisRef.element.dataset[name.short]
        return if (id != null) {
            registry.getOrElse(id, default).unsafeCast<T>()
        } else {
            default()
        }
    }
}