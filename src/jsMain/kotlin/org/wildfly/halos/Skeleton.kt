package org.wildfly.halos

import dev.fritz2.binding.const
import org.jboss.mvp.PlaceRequest
import org.jboss.mvp.renderAll
import org.patternfly.*
import org.patternfly.Orientation.VERTICAL

object Skeleton {
    val elements = renderAll(
            { pfAlertGroup(true) },
            {
                pfPage {
                    pfHeader {
                        pfBrand {
                            pfBrandLink("#${Places.SERVER}") {
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
                                }
                            }
                        }
                    }
                }
                pfSidebar {
                    pfNavigation(cdi().placeManager, VERTICAL) {
                        pfNavItems {
                            pfNavigationItem("Deployment", PlaceRequest(Places.DEPLOYMENT))
                            pfNavigationItem("Servers", PlaceRequest(Places.SERVER))
                            pfNavigationItem("Management Model", PlaceRequest(Places.MANAGEMENT))
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
