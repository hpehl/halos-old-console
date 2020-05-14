package org.wildfly.halos

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import mu.KotlinLogging
import org.wildfly.halos.dmr.Dispatcher
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.wildfly.halos.dmr.ResourceAddress
import org.wildfly.halos.dmr.operation
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
//<button class="pf-c-button pf-m-link pf-m-inline pf-m-inline" type="button">Inline link</button>
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
    val operation = operation(ResourceAddress.root(), READ_RESOURCE_OPERATION) {
        param("include-runtime", true)
    }
    GlobalScope.async {
        val node = Dispatcher.execute(operation)
        document.querySelector("#out")!!.textContent = node.asString()
    }
}
