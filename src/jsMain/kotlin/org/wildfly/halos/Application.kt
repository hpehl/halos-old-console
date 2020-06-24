package org.wildfly.halos

import dev.fritz2.binding.const
import org.jboss.mvp.PlaceRequest
import org.jboss.mvp.renderAll
import org.patternfly.Orientation.VERTICAL
import org.patternfly.component
import org.patternfly.layout
import org.patternfly.pfAlertGroup
import org.patternfly.pfBrand
import org.patternfly.pfBrandLink
import org.patternfly.pfHeader
import org.patternfly.pfHeaderTools
import org.patternfly.pfMain
import org.patternfly.pfNavItems
import org.patternfly.pfNavigation
import org.patternfly.pfNavigationItem
import org.patternfly.pfNotificationBadge
import org.patternfly.pfPage
import org.patternfly.pfSidebar

object Application {
    val skeleton = renderAll(
        { pfAlertGroup(true) },
        {
            pfPage {
                pfHeader {
                    pfBrand {
                        pfBrandLink("#${Places.server}") {
                            img("${"brand".component()} hal-logo") {
                                src = const("/halos-white.svg")
                            }
                        }
                    }
                    pfHeaderTools {
                        div("toolbar".layout()) {
                            div("toolbar".layout("group")) {
                                div("toolbar".layout("item")) {
                                    pfNotificationBadge()
                                }
                                div("toolbar".layout("item")) {
//                                pfIconDropdown<String>("server".pfIcon(), true) {
//                                    id = SERVER_DROPDOWN
//                                    onSelect = { console.log("Selected $it") }
//                                }
                                }
                            }
                        }
                    }
                }
                pfSidebar {
                    pfNavigation(cdi().placeManager, VERTICAL) {
                        pfNavItems {
                            pfNavigationItem("Servers", PlaceRequest(Places.server))
                            pfNavigationItem("Deployment", PlaceRequest(Places.deployment))
                            pfNavigationItem("Management Model", PlaceRequest(Places.management))
                        }
                    }
                }
                pfMain {
                    cdi().placeManager.manage(this)
                }
            }
        }
    )
}
