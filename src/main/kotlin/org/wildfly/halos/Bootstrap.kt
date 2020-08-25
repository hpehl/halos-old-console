package org.wildfly.halos

import dev.fritz2.binding.handledBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import org.w3c.dom.EventSource
import org.wildfly.halos.config.Endpoint

interface BootstrapTask {
    val name: String
    suspend fun execute()
}

class ServerSubscriptionTask : BootstrapTask {
    override val name = "subscribe to server updates"

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun execute() {
        val eventSource = EventSource("${Endpoint.instance}/subscribe")
        callbackFlow {
            eventSource.onmessage = { offer(it) }
            awaitClose { eventSource.close() }
        }.map {
            val (action, server) = it.data.toString().split(',')
            action to server
        } handledBy cdi().serverStore.serverEvent
    }
}
