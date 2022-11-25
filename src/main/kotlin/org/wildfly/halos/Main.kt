package org.wildfly.halos

import dev.fritz2.elemento.appendAll
import kotlinext.js.require
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun main() {
    require("@patternfly/patternfly/patternfly.css")
    require("@patternfly/patternfly/patternfly-addons.css")

    document.body!!.appendAll(Skeleton.elements)
    MainScope().launch {
        cdi().bootstrapTasks.forEach {
            val bootstrapTask = it()
            console.log("Execute ${bootstrapTask.name}")
            bootstrapTask.execute()
        }
    }
}
