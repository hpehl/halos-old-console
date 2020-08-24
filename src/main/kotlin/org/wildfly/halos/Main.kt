package org.wildfly.halos

import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun main() {
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")
    kotlinext.js.require("@patternfly/patternfly/patternfly-addons.css")

    Skeleton.elements.forEach {
        document.body!!.appendChild(it.domNode)
    }

    MainScope().launch {
        cdi().bootstrapTasks.forEach {
            val bootstrapTask = it()
            console.log("Execute ${bootstrapTask.name}")
            bootstrapTask.execute()
        }
    }
}
