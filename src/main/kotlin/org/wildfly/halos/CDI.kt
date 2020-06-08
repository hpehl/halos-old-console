package org.wildfly.halos

import org.jboss.dmr.Dispatcher
import org.jboss.mvp.PlaceManager
import org.jboss.mvp.PlaceRequest
import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.wildfly.halos.Ids.MAIN
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import org.wildfly.halos.server.ServerPresenter
import org.wildfly.halos.server.ServerSubscription

fun cdi(): CDI = CDIInstance

interface CDI {
    val dispatcher: Dispatcher
    val placeManager: PlaceManager
    val serverSubscription: ServerSubscription
    fun <P : Presenter<out View>> presenter(token: String): P
}

internal object CDIInstance : CDI {
    override val dispatcher = Dispatcher(Endpoint.management, Environment.cors)
    override val placeManager = PlaceManager("#$MAIN", PlaceRequest(ServerPresenter.TOKEN))
    override val serverSubscription = ServerSubscription()
    override fun <P : Presenter<out View>> presenter(token: String): P =
        checkNotNull(Presenter.lookup<P>(token)) {
            "No presenter found for $token"
        }
}
