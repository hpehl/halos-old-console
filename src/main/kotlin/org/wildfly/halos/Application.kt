package org.wildfly.halos

import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.id
import kotlinx.html.img
import org.jboss.elemento.Id
import org.jboss.mvp.PlaceRequest
import org.patternfly.component
import org.patternfly.layout
import org.patternfly.pfBrand
import org.patternfly.pfBrandLink
import org.patternfly.pfHeader
import org.patternfly.pfHeaderTools
import org.patternfly.pfIcon
import org.patternfly.pfIconDropdown
import org.patternfly.pfMain
import org.patternfly.pfNavItem
import org.patternfly.pfNavItems
import org.patternfly.pfNotificationBadge
import org.patternfly.pfPage
import org.patternfly.pfSidebar
import org.patternfly.pfToastAlertGroup
import org.patternfly.pfVerticalNav
import org.w3c.dom.HTMLElement
import org.wildfly.halos.model.ManagementModelPresenter
import org.wildfly.halos.server.ServerPresenter
import kotlin.browser.document

object Application {

    fun skeleton(): Array<HTMLElement> = with(document.create) {
        arrayOf(
            pfToastAlertGroup(),
            pfPage {
                pfHeader {
                    pfBrand {
                        pfBrandLink("#${ServerPresenter.TOKEN}") {
                            img(src = "/halos-white.svg", classes = "${"brand".component()} hal-logo")
                        }
                    }
                    pfHeaderTools {
                        div("toolbar".layout()) {
                            div("toolbar".layout("group")) {
                                div("toolbar".layout("item")) {
                                    pfNotificationBadge()
                                }
                                div("toolbar".layout("item")) {
                                    pfIconDropdown<String>("server".pfIcon()) {
                                        id = Ids.SERVER_DROPDOWN
                                        onSelect = { console.log("Selected $it") }
                                    }
                                }
                            }
                        }
                    }
                }
                pfSidebar {
                    pfVerticalNav<PlaceRequest> {
                        identifier = { Id.build(it.token) }
                        onSelect = {
                            cdi().placeManager.goto(it)
                        }
                        pfNavItems {
                            pfNavItem("Server", PlaceRequest(ServerPresenter.TOKEN))
                            pfNavItem("Management Model", PlaceRequest(ManagementModelPresenter.TOKEN))
                        }
                    }
                }
                pfMain(Ids.MAIN)
            }
        )
    }
}
