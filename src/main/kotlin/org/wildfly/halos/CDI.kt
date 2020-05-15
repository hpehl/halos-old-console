package org.wildfly.halos

import org.jboss.dmr.Dispatcher
import org.wildfly.halos.config.Endpoint

fun cdi(): CDI = CDIInstance

interface CDI {
    val dispatcher: Dispatcher

}

internal object CDIInstance : CDI {
    override val dispatcher = Dispatcher(Endpoint.management, true)
}