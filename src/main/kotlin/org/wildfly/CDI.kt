package org.wildfly

import org.wildfly.dmr.Dispatcher
import org.wildfly.halos.config.Endpoint

fun cdi(): CDI = CDIInstance

interface CDI {
    val dispatcher: Dispatcher

}

internal object CDIInstance : CDI {
    override val dispatcher = Dispatcher(Endpoint.management, true)
}