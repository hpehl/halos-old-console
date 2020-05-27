package org.wildfly.halos

import mu.KotlinLoggingConfiguration
import org.jboss.mvp.Presenter
import org.jboss.mvp.placeRequest
import org.patternfly.NavigationItem
import org.patternfly.pfNav
import org.wildfly.halos.config.Environment
import org.wildfly.halos.model.ManagementModelPresenter
import org.wildfly.halos.server.ServerPresenter
import kotlin.browser.document

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = Environment.logLevel
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")

    Presenter.register(ManagementModelPresenter.TOKEN, ::ManagementModelPresenter)
    Presenter.register(ServerPresenter.TOKEN, ::ServerPresenter)

    document.body!!.append(Application.skeleton())
    document.pfNav().autoSelect { NavigationItem("#${it.placeRequest()}") }
    cdi().placeManager.gotoCurrent()
}
