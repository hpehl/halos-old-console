package org.patternfly

import dev.fritz2.binding.SingleMountPoint
import dev.fritz2.dom.Listener
import dev.fritz2.dom.html.EventType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.Element
import org.w3c.dom.events.Event

internal fun Flow<Boolean>.bindAttr(name: String, target: Element?) =
    AttributeMountPoint(name, this, target)

internal class AttributeMountPoint(private val name: String, upstream: Flow<Boolean>, private val target: Element?) :
    SingleMountPoint<Boolean>(upstream) {
    override fun set(value: Boolean, last: Boolean?) {
        target?.setAttribute(name, value.toString())
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal fun <E : Event, T : Element> T.subscribe(type: EventType<E>): Listener<E, T> =
    Listener(callbackFlow {
        val listener: (Event) -> Unit = {
            offer(it.unsafeCast<E>())
        }
        addEventListener(type.name, listener)

        awaitClose { removeEventListener(type.name, listener) }
    })
