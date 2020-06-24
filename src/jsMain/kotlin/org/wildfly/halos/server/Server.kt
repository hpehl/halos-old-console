package org.wildfly.halos.server

import dev.fritz2.binding.handledBy
import dev.fritz2.dom.stopPropagation
import dev.fritz2.lenses.WithId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jboss.dmr.ModelDescriptionConstants.Companion.ATTRIBUTES_ONLY
import org.jboss.dmr.ModelDescriptionConstants.Companion.INCLUDE_RUNTIME
import org.jboss.dmr.ModelDescriptionConstants.Companion.LAUNCH_TYPE
import org.jboss.dmr.ModelDescriptionConstants.Companion.PRODUCT_NAME
import org.jboss.dmr.ModelDescriptionConstants.Companion.PRODUCT_VERSION
import org.jboss.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.jboss.dmr.ModelDescriptionConstants.Companion.RELEASE_VERSION
import org.jboss.dmr.ModelDescriptionConstants.Companion.RESULT
import org.jboss.dmr.ModelDescriptionConstants.Companion.RUNNING_MODE
import org.jboss.dmr.ModelDescriptionConstants.Companion.SERVER_STATE
import org.jboss.dmr.ModelDescriptionConstants.Companion.SUSPEND_STATE
import org.jboss.dmr.ModelNode
import org.jboss.dmr.NamedNode
import org.jboss.dmr.ResourceAddress
import org.jboss.dmr.op
import org.jboss.dmr.params
import org.jboss.elemento.Id
import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.jboss.mvp.renderAll
import org.patternfly.DataListDisplay
import org.patternfly.Drawer
import org.patternfly.Notification
import org.patternfly.SelectionMode
import org.patternfly.Severity
import org.patternfly.Size
import org.patternfly.Style
import org.patternfly.chart.pfcDonutUtilization
import org.patternfly.dd
import org.patternfly.dt
import org.patternfly.fas
import org.patternfly.layout
import org.patternfly.modifier
import org.patternfly.pfButton
import org.patternfly.pfContent
import org.patternfly.pfDataList
import org.patternfly.pfDataListItemAction
import org.patternfly.pfDataListItemCell
import org.patternfly.pfDataListItemContent
import org.patternfly.pfDataListItemRow
import org.patternfly.pfDrawer
import org.patternfly.pfDrawerActions
import org.patternfly.pfDrawerBody
import org.patternfly.pfDrawerClose
import org.patternfly.pfDrawerContent
import org.patternfly.pfDrawerHead
import org.patternfly.pfDrawerMain
import org.patternfly.pfDrawerPanel
import org.patternfly.pfEmptyState
import org.patternfly.pfEmptyStateBody
import org.patternfly.pfSection
import org.patternfly.pfTitle
import org.patternfly.util
import org.wildfly.halos.Places
import org.wildfly.halos.cdi
import org.wildfly.halos.dmr
import react.dom.render
import kotlin.browser.document

fun readServers() {
    dmr((ResourceAddress.root() op READ_RESOURCE_OPERATION) params {
        +ATTRIBUTES_ONLY
        +INCLUDE_RUNTIME
    }).map { result ->
        result.asPropertyList().map { property ->
            Server(property.name, property.value[RESULT])
        }
    } handledBy cdi().serverStore.update
}

class Server(val registeredName: String, node: ModelNode) : NamedNode(node), WithId {
    override val id = name
}

class ServerPresenter : Presenter<ServerView> {

    override val token = Places.server
    override val view = ServerView()

    override fun show() {
        readServers()
        view.drawer?.let {
            cdi().serverStore.selection.map { true } handledBy it.store.update
        }
        MainScope().launch {
            cdi().serverStore.selection.collect {
                render(document.getElementById("server-donut1")) {
                    pfcDonutUtilization {
                        attrs {
                            constrainToVisibleArea = true
                            data = js("""{ x: 'Memory', y: 35 }""")
                            subTitle = it.name
                            title = "100"
                        }
                    }
                }
                render(document.getElementById("server-donut2")) {
                    pfcDonutUtilization {
                        attrs {
                            constrainToVisibleArea = true
                            data = js("""{ x: 'Threads', y: 66 }""")
                            subTitle = it.name
                            title = "100"
                        }
                    }
                }
            }
        }
    }
}

class ServerView : View {
    private val serverDisplay: DataListDisplay<Server> = {
        {
            pfDataListItemRow {
                pfDataListItemContent {
                    pfDataListItemCell {
                        div("flex".layout() + " " + "column".modifier()) {
                            div {
                                p { text(it.name) }
                                small("mr-sm".util()) {
                                    domNode.title = "Product Version"
                                    text(it[PRODUCT_VERSION].asString())
                                }
                                small {
                                    domNode.title = "Release Version"
                                    text(it[RELEASE_VERSION].asString())
                                }
                            }
                            div("flex".layout()) {
                                div {
                                    text("State: ")
                                    span {
                                        domNode.title = "running mode"
                                        text(it[RUNNING_MODE].asString().toLowerCase())
                                    }
                                    text(" / ")
                                    span {
                                        domNode.title = "server state"
                                        text(it[SERVER_STATE].asString().toLowerCase())
                                    }
                                    text(" / ")
                                    span {
                                        domNode.title = "suspend state"
                                        text(it[SUSPEND_STATE].asString().toLowerCase())
                                    }
                                }
                            }
                        }
                    }
                }
                pfDataListItemAction {
                    pfButton(Style.primary, "Restart") {
                        clicks.stopPropagation().map {
                            Notification(Severity.INFO, "Restart not yet implemented")
                        } handledBy Notification.store.add
                    }
                    pfButton(Style.secondary, "Suspend") {
                        clicks.stopPropagation().map {
                            Notification(Severity.INFO, "Suspend not yet implemented")
                        } handledBy Notification.store.add
                    }
                }
            }
        }
    }

    internal var drawer: Drawer? = null

    override val elements = renderAll(
        {
            pfSection("light".modifier(), "fill".modifier()) {
                classMap = cdi().serverStore.empty.map { noServers -> mapOf("display-none".util() to !noServers) }
                pfEmptyState("server".fas(), "No Servers", Size.lg) {
                    pfEmptyStateBody {
                        p {
                            text("No servers found. Please manage your servers in OpenShift using the WildFly operator.")
                        }
                        p {
                            text("This view will update automatically, once there are running servers.")
                        }
                    }
                }
            }
        },
        {
            pfSection("light".modifier()) {
                classMap = cdi().serverStore.empty.map { noServers -> mapOf("display-none".util() to noServers) }
                pfContent {
                    h1 { text("Servers") }
                    p { text("The list of servers managed by the WildFly operator.") }
                }
            }
        },
        {
            pfSection("no-padding".modifier(), "padding-on-md".modifier()) {
                classMap = cdi().serverStore.empty.map { noServers -> mapOf("display-none".util() to noServers) }
                drawer = pfDrawer {
                    pfDrawerMain {
                        pfDrawerContent {
                            domNode.style.background = "none"
                            pfDrawerBody {
                                pfDataList(SelectionMode.SINGLE, cdi().serverStore) {
                                    identifier = { Id.asId(it.name) }
                                    display = serverDisplay
                                }
                            }
                        }
                        pfDrawerPanel {
                            val currentServer = cdi().serverStore.selection
                            pfDrawerBody {
                                pfDrawerHead {
                                    pfTitle(size = Size.lg) {
                                        currentServer.map { it.name }.bind()
                                    }
                                    pfDrawerActions {
                                        pfDrawerClose()
                                    }
                                }
                            }
                            pfDrawerBody {
                                pfContent {
                                    dl {
                                        dt { text("Product Name") }
                                        dd { currentServer.map { it[PRODUCT_NAME].asString() }.bind() }
                                        dt { text("Product Version") }
                                        dd { currentServer.map { it[PRODUCT_VERSION].asString() }.bind() }
                                        dt { text("Launch Type") }
                                        dd { currentServer.map { it[LAUNCH_TYPE].asString() }.bind() }
                                        dt { text("UUID") }
                                        dd { currentServer.map { it["uuid"].asString() }.bind() }
                                    }
                                }
                            }
                            pfDrawerBody {
                                div("pf-l-flex pf-m-justify-content-space-between") {
                                    div(id = "server-donut1") {
                                        domNode.style.width = "175px"
                                        domNode.style.height = "175px"
                                    }
                                    div(id = "server-donut2") {
                                        domNode.style.width = "175px"
                                        domNode.style.height = "175px"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
