package org.wildfly.halos

import dev.fritz2.dom.html.render
import org.jboss.mvp.PlaceManager
import org.jboss.mvp.PlaceRequest
import org.jboss.mvp.Presenter
import org.patternfly.pfContent
import org.patternfly.pfSection
import org.wildfly.halos.deployment.DeploymentPresenter
import org.wildfly.halos.model.ManagementModelPresenter
import org.wildfly.halos.server.ServerPresenter
import org.wildfly.halos.server.ServerStore

fun cdi(): CDI = CDIInstance

interface CDI {
    val bootstrapTasks: List<() -> BootstrapTask>
    val placeManager: PlaceManager
    val serverStore: ServerStore
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
                    h1 { text("Not Found") }
                    p { text("The requested page cannot be found!") }
                }
            }
        }
    }
    override val serverStore = ServerStore()
}
