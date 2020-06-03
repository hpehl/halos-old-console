package org.wildfly.halos

import mu.KotlinLoggingConfiguration
import org.jboss.mvp.PlaceRequest
import org.jboss.mvp.Presenter
import org.patternfly.pfNav
import org.wildfly.halos.config.Environment
import org.wildfly.halos.model.ManagementModelPresenter
import org.wildfly.halos.server.ServerPresenter
import kotlin.browser.document

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = Environment.logLevel
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")
    kotlinext.js.require("@patternfly/patternfly/patternfly-addons.css")

    Presenter.register(ManagementModelPresenter.TOKEN, ::ManagementModelPresenter)
    Presenter.register(ServerPresenter.TOKEN, ::ServerPresenter)

    document.body!!.append(*Application.skeleton())
    cdi().placeManager.gotoCurrent()
    document.pfNav<PlaceRequest>().select(cdi().placeManager.currentPlace, false)
    document.pfNav<PlaceRequest>().autoSelect { PlaceRequest.fromEvent(it) }
}
