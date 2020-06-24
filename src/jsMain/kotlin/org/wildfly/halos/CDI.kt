package org.wildfly.halos

import dev.fritz2.dom.html.render
import org.jboss.mvp.PlaceManager
import org.jboss.mvp.PlaceRequest
import org.jboss.mvp.Presenter
import org.patternfly.DataListStore
import org.patternfly.pfContent
import org.patternfly.pfSection
import org.wildfly.halos.deployment.DeploymentPresenter
import org.wildfly.halos.model.ManagementModelPresenter
import org.wildfly.halos.server.Server
import org.wildfly.halos.server.ServerPresenter

fun cdi(): CDI = CDIInstance

interface CDI {
    val bootstrapTasks: List<() -> BootstrapTask>
    val placeManager: PlaceManager
    val serverStore: DataListStore<Server>
}

internal object CDIInstance : CDI {
    init {
        Presenter.register(Places.server, ::ServerPresenter)
        Presenter.register(Places.deployment, ::DeploymentPresenter)
        Presenter.register(Places.management, ::ManagementModelPresenter)
    }

    override val bootstrapTasks = listOf(::ServerSubscriptionTask)
    override val placeManager = PlaceManager(PlaceRequest(Places.server)) {
        render {
            pfSection {
                pfContent {
                    h1 { text("Not Found") }
                    p { text("The requested page cannot be found!") }
                }
            }
        }
    }
    override val serverStore = DataListStore<Server>()
}
