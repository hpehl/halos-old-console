package org.wildfly.halos

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import org.patternfly.Notification
import org.w3c.dom.EventSource
import org.w3c.dom.EventSourceInit
import org.w3c.dom.MessageEvent
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import org.wildfly.halos.server.readServers

interface BootstrapTask {
    val name: String
    suspend fun execute()
}

class ServerSubscriptionTask : BootstrapTask {
    override val name = "subscribe to server updates"

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun execute() {
        val eventSource = EventSource("${Endpoint.instance}/subscribe", EventSourceInit(Environment.cors))
        callbackFlow {
            eventSource.onmessage = { offer(it as MessageEvent) }
            awaitClose { eventSource.close() }
        }.collect {
            val (action, server) = it.data.toString().split(',')
            console.log("Got subscription event: $action $server")
            when (action) {
                "ADDED" -> {
                    readServers()
                    Notification.info("Added server $server")
                }
                "REMOVED" -> {
                    // TODO In order for remove handler to work,
                    //  the WildFly docker container has to be
                    //  started with -Djboss.server.name=<name>
//                    flowOf(server) handledBy cdi().serverStore.remove
                    readServers()
                    Notification.info("Removed server $server")
                }
            }
        }
    }
}
