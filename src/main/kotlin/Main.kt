package org.wildfly.halos

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.div
import org.wildfly.halos.dmr.*
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_DESCRIPTION_OPERATION
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import kotlin.browser.document

fun main() {
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")
    console.log("Hello World")

    document.body!!.append.div {
        h1 {
            +"Welcome to Kotlin/JS!"
        }
        p {
            +"Fancy joining this year's "
            a("https://kotlinconf.com/") {
                +"KotlinConf"
            }
            +"?"
        }
        pre {
            id = "out"
        }
    }

    val operation = operation(ResourceAddress.root(), READ_RESOURCE_OPERATION) {
        param("include-runtime", true)
    }
    console.log("Operation: $operation")
    GlobalScope.async {
        val node = Dispatcher.execute(operation)
        document.querySelector("#out")!!.textContent = node.asString()
    }
}