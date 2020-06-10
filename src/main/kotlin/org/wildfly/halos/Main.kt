package org.wildfly.halos

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import org.jboss.mvp.PlaceRequest
import org.patternfly.pfNav
import org.wildfly.halos.config.Environment
import kotlin.browser.document

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = Environment.logLevel
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")
    kotlinext.js.require("@patternfly/patternfly/patternfly-addons.css")

    document.body!!.append(*Application.skeleton())
    cdi().placeManager.gotoCurrent()
    document.pfNav<PlaceRequest>().autoSelect { PlaceRequest.fromEvent(it) }
    document.pfNav<PlaceRequest>().select(cdi().placeManager.currentPlace, false)

    GlobalScope.launch {
        val logger = KotlinLogging.logger("bootstrap")
        cdi().bootstrapTasks.forEach {
            val bootstrapTask = it()
            logger.info { "Execute ${bootstrapTask.name}" }
            bootstrapTask.execute()
        }
    }
}
