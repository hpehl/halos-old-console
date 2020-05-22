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
import kotlin.browser.document

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
        pfSidebar {
            pfVerticalNav {
                // simple
                pfNavItems {
                    pfNavItem(NavigationItem("server-0", "Server", "#"))
                    pfNavItem(NavigationItem("mm-0", "Management Model", "#"))
                }
                // grouped
                pfNavGroup("Static Group 1") {
                    pfNavItems {
                        pfNavItem(NavigationItem("server-1", "Server", "#"))
                        pfNavItem(NavigationItem("mm-1", "Management Model", "#"))
                    }
                }
                pfNavGroup("Static Group 2") {
                    pfNavItems {
                        pfNavItem(NavigationItem("server-2", "Server", "#"))
                        pfNavItem(NavigationItem("mm-2", "Management Model", "#"))
                    }
                }
                // expandable
                pfNavItems {
                    pfNavExpandableGroup("Ex Group 1") {
                        pfNavItem(NavigationItem("server-3", "Server", "#"))
                        pfNavItem(NavigationItem("mm-3", "Management Model", "#"))
                    }
                    pfNavExpandableGroup("Ex Group 2") {
                        pfNavItem(NavigationItem("server-4", "Server", "#"))
                        pfNavItem(NavigationItem("mm-4", "Management Model", "#"))
                    }
                }
            }
        }
        pfMain("halos-main") {
            pfSection("light".modifier()) {
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
                pfDataList {
                    id = "users"
                }
            }
            pfSection("light".modifier()) {
                pfAlert(Severity.warning, "My first warning alert", true) {
                    pfAlertDescription { +"My description" }
                    onClose = { console.log("About to close alert...") }
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

    document.pfNav().select(NavigationItem("mm-4"))

    val dataProvider = DataProvider<User> { Id.build("item", it.username) }
    // TODO Make renderer a DataListTag constructor parameter
    val renderer: DataListRenderer<User> = { user, dp ->
        {
            pfItemRow {
                pfItemContent {
                    pfCell {
                        span {
                            id = dp.identifier(user)
                            +user.username
                        }
                    }
                    pfCell { +user.email }
                }
            }
        }
    }
    val dataList = document.getElementById("users")!!.pfDataList(dataProvider, renderer)
    dataProvider.bind(dataList)
    dataProvider.update(users)
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

data class User(val username: String, val name: String, val email: String)

val users = listOf(
    User("johndoe", "John Doe", "john@doe.com"),
    User("hpehl", "Harald Pehl", "harald.pehl@gmakil.com")
)
