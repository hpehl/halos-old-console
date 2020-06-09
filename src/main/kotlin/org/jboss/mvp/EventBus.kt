package org.jboss.mvp

import kotlin.reflect.KClass

typealias EventHandler<T> = (event: T) -> Unit

class EventBus {
    private val handlers: MutableMap<KClass<out Any>, MutableList<EventHandler<Any>>> = mutableMapOf()

    fun <T : Any> subscribe(eventType: KClass<out T>, handler: EventHandler<T>) {
        @Suppress("UNCHECKED_CAST")
        handlers.getOrPut(eventType) { mutableListOf() }.add(handler as EventHandler<Any>)
    }

    fun <T : Any> post(event: T) {
        handlers[event::class]?.forEach {
            it(event)
        }
    }
}
