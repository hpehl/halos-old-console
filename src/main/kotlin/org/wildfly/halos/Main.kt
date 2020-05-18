package org.wildfly.halos

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import org.jboss.dmr.ModelDescriptionConstants.Companion.INCLUDE_RUNTIME
import org.jboss.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.jboss.dmr.ModelDescriptionConstants.Companion.RECURSIVE_DEPTH
import org.jboss.dmr.op
import org.jboss.dmr.params
import org.patternfly.*
import org.patternfly.Style.primary
import org.patternfly.Style.secondary
import kotlin.browser.document

@ExperimentalStdlibApi
fun main() {
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")

    document.body!!.append.pfPage {
        pfHeader {
            pfBrand {
                a("#", classes = "page".component("header", "brand", "link")) {
                    img(src = "https://www.patternfly.org/assets/images/PF-Masthead-Logo.svg") {
                        classes += "brand".component()
                    }
                }
            }
        }
        pfMain("halos-main") {
            pfSection {
                classes += "light".modifier()
                pfContent {
                    h1 {
                        classes += "pf-c-title"
                        +"halOS"
                    }
                    p { +"WildFly management console for OpenShift." }
                    p {
                        +"Execute an "
                        pfLinkButton(text = "operation", inline = true) {
                            onClickFunction = { _ -> readResource() }
                        }
                        +"."
                    }
                }
            }
            pfSection {
                pfContent {
                    pre {
                        id = "out"
                    }
                }
            }
        }
    }
}

fun readResource() {
    GlobalScope.launch {
        val operation = ("subsystem=ee" op READ_RESOURCE_OPERATION) params {
            +INCLUDE_RUNTIME
            +(RECURSIVE_DEPTH to 1)
        }
        val node = cdi().dispatcher.execute(operation)
        document.querySelector("#out")!!.textContent = node.toString()
    }
}
