package org.wildfly.halos

import org.patternfly.Notification
import org.w3c.dom.EventSource
import org.w3c.dom.EventSourceInit
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import org.wildfly.halos.server.refreshServer

interface BootstrapTask {
    val name: String
    suspend fun execute()
}

class ServerSubscriptionTask : BootstrapTask {
    override val name = "subscribe to server updates"

    override suspend fun execute() {
        val eventSource = EventSource("${Endpoint.instance}/subscribe", EventSourceInit(Environment.cors))
        eventSource.onmessage = { message ->
            val (action, server) = message.data.toString().split(',')
            when (action) {
                "ADDED" -> {
                    refreshServer()
                    Notification.info("Added server $server")
                }
                "REMOVED" -> {
                    // TODO In order for remove handler to work,
                    //  start the WildFly docker container with
                    //  -Djboss.server.name=<name>
//                    flowOf(server) handledBy cdi().serverStore.remove
                    refreshServer()
                    Notification.info("Removed server $server")
                }
            }
        }
    }
}
