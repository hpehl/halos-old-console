package org.wildfly.halos

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import mu.KotlinLogging
import org.wildfly.dmr.*
import org.wildfly.dmr.ModelDescriptionConstants.Companion.ACCESS_CONSTRAINTS
import org.wildfly.dmr.ModelDescriptionConstants.Companion.INCLUDE_RUNTIME
import org.wildfly.dmr.ModelDescriptionConstants.Companion.NONE
import org.wildfly.dmr.ModelDescriptionConstants.Companion.OPERATIONS
import org.wildfly.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_DESCRIPTION_OPERATION
import org.wildfly.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.wildfly.dmr.ModelDescriptionConstants.Companion.RECURSIVE_DEPTH
import org.wildfly.dmr.ResourceAddress.Companion.root
import org.wildfly.halos.config.Endpoint
import kotlin.browser.document

private val logger = KotlinLogging.logger("main")

fun main() {
    kotlinext.js.require("@patternfly/patternfly/patternfly.css")
    logger.info { "halOS console starting up..." }

    document.body!!.append.div {
        classes += "pf-c-page"
        header {
            classes += "pf-c-page__header"
            attributes["role"] = "banner"
            div {
                classes += "pf-c-page__header-brand"
                a("#") {
                    classes += "pf-c-page__header-brand-link"
                    img(src = "https://www.patternfly.org/assets/images/PF-Masthead-Logo.svg") {
                        classes += "pf-c-brand"
                    }
                }
            }
        }
        main {
            id = "halos-main"
            classes += "pf-c-page__main"
            attributes["role"] = "main"
            tabIndex = "-1"
            section {
                classes = setOf("pf-c-page__main-section", "pf-m-light")
                div {
                    classes += "pf-c-content"
                    h1 {
                        classes += "pf-c-title"
                        +"halOS"
                    }
                    p { +"WildFly management console for OpenShift." }
                    p {
                        +"Execute "
                        button {
                            classes = setOf("pf-c-button", "pf-m-link", "pf-m-inline", "pf-m-inline")
                            type = ButtonType.button
                            onClickFunction = { _ -> readResource() }
                            +"read-resource"
                        }
                        +" operation on root resource"
                    }
                }
            }
            section {
                classes += "pf-c-page__main-section"
                div {
                    classes += "pf-c-content"
                    pre {
                        id = "out"
                    }
                }
            }
        }
    }
}

private fun readResource() {
    GlobalScope.launch {
        val operation = ("/subsystem=undertow" exe READ_RESOURCE_OPERATION) params {
            +INCLUDE_RUNTIME
            +(RECURSIVE_DEPTH to 1)
        }
        val op2 = (root() exe READ_RESOURCE_DESCRIPTION_OPERATION) params {
            +OPERATIONS
            +(ACCESS_CONSTRAINTS to NONE)
        }
        val dispatcher = Dispatcher(Endpoint.management, true)
        val node = dispatcher.execute(operation)
        document.querySelector("#out")!!.textContent = node.toString()
    }
}
