package org.wildfly.halos

import dev.fritz2.elemento.elements
import dev.fritz2.mvp.PlaceRequest
import org.patternfly.layout
import org.patternfly.pfAlertGroup
import org.patternfly.pfBrand
import org.patternfly.pfBrandContainer
import org.patternfly.pfBrandLink
import org.patternfly.pfHeader
import org.patternfly.pfHeaderTools
import org.patternfly.pfMain
import org.patternfly.pfNavigationItem
import org.patternfly.pfNavigationItems
import org.patternfly.pfNotificationBadge
import org.patternfly.pfPage
import org.patternfly.pfSidebar
import org.patternfly.pfSidebarBody
import org.patternfly.pfVerticalNavigation

object Skeleton {
    val elements = elements {
        pfAlertGroup(true)
        pfPage {
            pfHeader {
                pfBrandContainer {
                    pfBrandLink("#${Places.SERVER}") {
                        pfBrand("/halos-white.svg")
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
                pfSidebarBody {
                    pfVerticalNavigation(cdi().placeManager.router) {
                        pfNavigationItems {
                            pfNavigationItem(PlaceRequest(Places.DEPLOYMENT), "Deployment")
                            pfNavigationItem(PlaceRequest(Places.SERVER), "Servers")
                            pfNavigationItem(PlaceRequest(Places.MANAGEMENT), "Management Model")
                        }
                    }
                }
            }
            pfMain {
                cdi().placeManager.manage(this)
            }
        }
    }
}