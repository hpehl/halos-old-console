package org.wildfly.halos

import org.jboss.dmr.Dispatcher
import org.wildfly.halos.Ids.MAIN
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import org.jboss.mvp.PlaceManager
import org.jboss.mvp.placeRequest

fun cdi(): CDI = CDIInstance

interface CDI {
    val dispatcher: Dispatcher
    val placeManager: PlaceManager
}

internal object CDIInstance : CDI {
    override val dispatcher = Dispatcher(Endpoint.management, Environment.cors)
    override val placeManager = PlaceManager("#$MAIN", "#server".placeRequest())
}