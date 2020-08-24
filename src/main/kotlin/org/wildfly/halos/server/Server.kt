package org.wildfly.halos.server

import dev.fritz2.binding.SimpleHandler
import dev.fritz2.binding.handledBy
import dev.fritz2.dom.html.render
import dev.fritz2.dom.stopPropagation
import dev.fritz2.lenses.IdProvider
import dev.fritz2.mvp.Presenter
import dev.fritz2.mvp.View
import dev.fritz2.remote.FetchException
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
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
import org.patternfly.DataListDisplay
import org.patternfly.DataListStore
import org.patternfly.Drawer
import org.patternfly.Id
import org.patternfly.Modifier.primary
import org.patternfly.Modifier.secondary
import org.patternfly.Notification
import org.patternfly.Severity
import org.patternfly.Size
import org.patternfly.fas
import org.patternfly.layout
import org.patternfly.modifier
import org.patternfly.pfButton
import org.patternfly.pfContent
import org.patternfly.pfDataList
import org.patternfly.pfDataListAction
import org.patternfly.pfDataListCell
import org.patternfly.pfDataListContent
import org.patternfly.pfDataListRow
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
import org.wildfly.halos.cdi
import org.wildfly.halos.dmr

suspend fun readServers(): List<Server> {
    val operation = (ResourceAddress.root() op READ_RESOURCE_OPERATION) params {
        +ATTRIBUTES_ONLY
        +INCLUDE_RUNTIME
    }
    return dmr(operation)
        .catch {
            if (it is FetchException && it.statusCode == 404.toShort()) {
                emit(ModelNode()) // will end up in an empty server list
            } else {
                console.error("Unable to execute $operation: ${it.message}")
            }
        }
        .map { result ->
            result.asPropertyList().map { property -> Server(property.name, property.value[RESULT]) }
        }
}

class Server(val registeredName: String, node: ModelNode) : NamedNode(node)

val serverId: IdProvider<Server, String> = { Id.build("srv", it.name) }

class ServerStore : DataListStore<Server>(serverId) {
    val r: SimpleHandler<Unit> = handle { }

    val serverEvent = handleAndOffer<Pair<String, String>, List<Server>> { _, event ->
        val (action, server) = event
        when (action) {
            "ADDED" -> "Added server $server"
            "REMOVED" -> "Removed server $server"
            else -> null
        }?.let { Notification.info(it) }
        listOf()
//        readServers()
    } andThen update

    val refresh = apply<Unit, List<Server>> { readServers() } andThen update
}

@OptIn(InternalCoroutinesApi::class)
class ServerPresenter : Presenter<ServerView> {

    override val view = ServerView()

    override fun show() {
        readServers() handledBy cdi().serverStore.update
        view.drawer?.let {
            cdi().serverStore.selection.map { true } handledBy it.expanded.update
        }
        MainScope().launch {
            cdi().serverStore.selection.collect {
                console.log("Updating server $it not yet implemented!")
            }
        }
    }
}

class ServerView : View {
    private val serverDisplay: DataListDisplay<Server> = {
        {
            pfDataListRow {
                pfDataListContent {
                    pfDataListCell {
                        div("flex".layout() + " " + "column".modifier()) {
                            div {
                                p { +it.name }
                                small("mr-sm".util()) {
                                    domNode.title = "Product Version"
                                    +it[PRODUCT_VERSION].asString()
                                }
                                small {
                                    domNode.title = "Release Version"
                                    +it[RELEASE_VERSION].asString()
                                }
                            }
                            div("flex".layout()) {
                                div {
                                    +"State: "
                                    span {
                                        domNode.title = "running mode"
                                        +it[RUNNING_MODE].asString().toLowerCase()
                                    }
                                    +" / "
                                    span {
                                        domNode.title = "server state"
                                        +it[SERVER_STATE].asString().toLowerCase()
                                    }
                                    +" / "
                                    span {
                                        domNode.title = "suspend state"
                                        +it[SUSPEND_STATE].asString().toLowerCase()
                                    }
                                }
                            }
                        }
                    }
                }
                pfDataListAction {
                    pfButton(primary) {
                        +"Restart"
                        clicks.stopPropagation().map {
                            Notification(Severity.INFO, "Restart not yet implemented")
                        } handledBy Notification.store.add
                    }
                    pfButton(secondary) {
                        +"Suspend"
                        clicks.stopPropagation().map {
                            Notification(Severity.INFO, "Suspend not yet implemented")
                        } handledBy Notification.store.add
                    }
                }
            }
        }
    }

    internal var drawer: Drawer? = null

    override val elements = listOf(
        render {
            pfSection("light".modifier(), "fill".modifier()) {
                classMap = cdi().serverStore.empty.map { noServers -> mapOf("display-none".util() to !noServers) }
                pfEmptyState("server".fas(), "No Servers") {
                    pfEmptyStateBody {
                        p {
                            +"No servers found. Please manage your servers in OpenShift using the WildFly operator."
                        }
                        p {
                            +"This view will update automatically, once there are servers available."
                        }
                    }
                    pfButton(primary) {
                        +"Refresh"
                        clicks handledBy cdi().serverStore.refresh
                    }
                }
            }
        },
        render {
            pfSection("light".modifier()) {
                classMap = cdi().serverStore.empty.map { noServers -> mapOf("display-none".util() to noServers) }
                pfContent {
                    h1 { +"Servers" }
                    p { +"The list of servers managed by the WildFly operator." }
                }
            }
        },
        render {
            pfSection("no-padding".modifier(), "padding-on-md".modifier()) {
                classMap = cdi().serverStore.empty.map { noServers -> mapOf("display-none".util() to noServers) }
                drawer = pfDrawer {
                    pfDrawerMain {
                        pfDrawerContent {
                            domNode.style.background = "none"
                            pfDrawerBody {
                                pfDataList(serverId, cdi().serverStore) {
                                    display = serverDisplay
                                }
                            }
                        }
                        pfDrawerPanel {
                            val currentServer = cdi().serverStore.selection
                            pfDrawerBody {
                                pfDrawerHead {
                                    pfTitle(size = Size.LG) {
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
                                        dt { +"Product Name" }
                                        dd { currentServer.map { it[PRODUCT_NAME].asString() }.bind() }
                                        dt { +"Product Version" }
                                        dd { currentServer.map { it[PRODUCT_VERSION].asString() }.bind() }
                                        dt { +"Launch Type" }
                                        dd { currentServer.map { it[LAUNCH_TYPE].asString() }.bind() }
                                        dt { +"UUID" }
                                        dd { currentServer.map { it["uuid"].asString() }.bind() }
                                    }
                                }
                            }
                            pfDrawerBody {
                                div("pf-l-flex pf-m-justify-content-space-between") {
                                    div(id = "server-donut1") {
                                        domNode.style.width = "175px"
                                        domNode.style.height = "175px"
                                        +"Donut 1"
                                    }
                                    div(id = "server-donut2") {
                                        domNode.style.width = "175px"
                                        domNode.style.height = "175px"
                                        +"Donut 1"
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
