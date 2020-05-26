package org.wildfly.halos

import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.dom.create
import kotlinx.html.img
import mu.KotlinLogging
import org.patternfly.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.Window
import org.wildfly.halos.mvp.placeRequest
import kotlin.browser.document
import kotlin.browser.window

object Application {

    private val logger = KotlinLogging.logger("app")

    init {
        window.addEventListener("popstate", {
            val place = (it.target as Window).location.hash.placeRequest()
            logger.debug { "Popstate event: select navigation item for place request $place" }
            document.pfNav().select("#${place.token}")
        })
    }

    fun skeleton(): HTMLElement = document.create.pfPage {
        pfHeader {
            pfBrand {
                a("#server", classes = "page".component("header", "brand", "link")) {
                    img(src = "/halos-white.svg", classes = "hal-logo") {
                        classes += "brand".component()
                    }
                }
            }
        }
        pfSidebar {
            pfVerticalNav {
                pfNavItems {
                    pfNavItem(NavigationItem("#server", "Server"))
                    pfNavItem(NavigationItem("#mm", "Management Model"))
                }
            }
        }
        pfMain(Ids.MAIN)
    }
}