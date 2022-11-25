package org.wildfly.halos

import dev.fritz2.dom.html.render
import dev.fritz2.mvp.PlaceManager
import dev.fritz2.mvp.PlaceRequest
import dev.fritz2.mvp.Presenter
import org.patternfly.ItemStore
import org.patternfly.pfContent
import org.patternfly.pfSection
import org.wildfly.halos.deployment.DeploymentPresenter
import org.wildfly.halos.model.ManagementModelPresenter
import org.wildfly.halos.server.Server
import org.wildfly.halos.server.ServerPresenter
import org.wildfly.halos.server.serverId

fun cdi(): CDI = CDIInstance

interface CDI {
    val bootstrapTasks: List<() -> BootstrapTask>
    val placeManager: PlaceManager
    val serverStore: ItemStore<Server>
}

internal object CDIInstance : CDI {
    init {
        Presenter.register(Places.SERVER, ::ServerPresenter)
        Presenter.register(Places.DEPLOYMENT, ::DeploymentPresenter)
        Presenter.register(Places.MANAGEMENT, ::ManagementModelPresenter)
    }

    override val bootstrapTasks = listOf(::ServerSubscriptionTask)

    override val placeManager = PlaceManager(PlaceRequest(Places.SERVER)) {
        render {
            pfSection {
                pfContent {
                    h1 { +"Not Found" }
                    p { +"The requested page cannot be found!" }
                }
            }
        }
    }

    override val serverStore = ItemStore(serverId)
}
