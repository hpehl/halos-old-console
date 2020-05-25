package org.wildfly.halos

import org.jboss.dmr.Dispatcher
import org.wildfly.halos.Ids.MAIN
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import org.wildfly.halos.mvp.PlaceManager
import org.wildfly.halos.mvp.placeRequest

fun cdi(): CDI = CDIInstance

interface CDI {
    val dispatcher: Dispatcher
    val placeManager: PlaceManager
}

internal object CDIInstance : CDI {
    override val dispatcher = Dispatcher(Endpoint.management, Environment.cors)
    override val placeManager = PlaceManager("#$MAIN", "#server".placeRequest())
}