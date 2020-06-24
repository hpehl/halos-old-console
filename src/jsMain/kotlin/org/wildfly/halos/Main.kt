package org.wildfly.halos

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import org.wildfly.halos.config.Environment
import kotlin.browser.document

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = Environment.logLevel
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")
    kotlinext.js.require("@patternfly/patternfly/patternfly-addons.css")

    Application.skeleton.forEach {
        document.body!!.appendChild(it.domNode)
    }

    MainScope().launch {
        val logger = KotlinLogging.logger("bootstrap")
        cdi().bootstrapTasks.forEach {
            val bootstrapTask = it()
            logger.info { "Execute ${bootstrapTask.name}" }
            bootstrapTask.execute()
        }
    }
}
