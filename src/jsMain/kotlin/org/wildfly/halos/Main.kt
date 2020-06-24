package org.wildfly.halos

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.browser.document

fun main() {
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")
    kotlinext.js.require("@patternfly/patternfly/patternfly-addons.css")

    Application.skeleton.forEach {
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
