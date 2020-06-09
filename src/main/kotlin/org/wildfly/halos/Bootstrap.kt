package org.wildfly.halos

import org.jboss.dmr.ModelDescriptionConstants
import org.jboss.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.jboss.dmr.ModelDescriptionConstants.Companion.RESULT
import org.jboss.dmr.ResourceAddress
import org.jboss.dmr.op
import org.jboss.dmr.params
import org.w3c.dom.EventSource
import org.w3c.dom.EventSourceInit
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import org.wildfly.halos.server.Server
import org.wildfly.halos.server.ServerUpdate
import org.wildfly.halos.server.Servers

interface BootstrapTask {
    val name: String
    suspend fun execute()
}

class ReadServerTask : BootstrapTask {
    private val dispatcher = cdi().dispatcher
    private val eventBus = cdi().eventBus

    override val name = "read servers"

    override suspend fun execute() {
        val operation = (ResourceAddress.root() op READ_RESOURCE_OPERATION) params {
            +ModelDescriptionConstants.ATTRIBUTES_ONLY
            +ModelDescriptionConstants.INCLUDE_RUNTIME
        }
        val node = dispatcher.execute(operation)
        eventBus.post(Servers(node.asPropertyList().map { Server(it.value[RESULT]) }))
    }
}

class ServerSubscriptionTask : BootstrapTask {
    private val eventBus = cdi().eventBus

    override val name = "subscribe to server updates"

    override suspend fun execute() {
        val eventSource = EventSource("${Endpoint.instance}/subscribe", EventSourceInit(Environment.cors))
        eventSource.onmessage = {
            eventBus.post(ServerUpdate(it))
        }
    }
}
