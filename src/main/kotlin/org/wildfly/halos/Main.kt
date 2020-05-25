package org.wildfly.halos

import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import org.wildfly.halos.model.ManagementModelPresenter
import org.wildfly.halos.mvp.Presenter
import org.wildfly.halos.server.ServerPresenter
import kotlin.browser.document

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")
    document.body!!.append(Application.page())

    Presenter.register(ManagementModelPresenter())
    Presenter.register(ServerPresenter())

    cdi().placeManager.gotoCurrent()
}
