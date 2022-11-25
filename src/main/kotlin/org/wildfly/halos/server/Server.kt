package org.wildfly.halos.server

import dev.fritz2.binding.action
import dev.fritz2.binding.handledBy
import dev.fritz2.dom.stopPropagation
import dev.fritz2.elemento.Id
import dev.fritz2.elemento.elements
import dev.fritz2.lenses.IdProvider
import dev.fritz2.mvp.Presenter
import dev.fritz2.mvp.View
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.patternfly.Drawer
import org.patternfly.Items
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
import org.patternfly.pfDataListItem
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
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.ATTRIBUTES_ONLY
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.INCLUDE_RUNTIME
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.LAUNCH_TYPE
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.PRODUCT_NAME
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.PRODUCT_VERSION
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.RELEASE_VERSION
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.RESULT
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.RUNNING_MODE
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.SERVER_STATE
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.SUSPEND_STATE
import org.wildfly.halos.dmr.ModelNode
import org.wildfly.halos.dmr.NamedNode
import org.wildfly.halos.dmr.ResourceAddress
import org.wildfly.halos.dmr.op
import org.wildfly.halos.dmr.params

suspend fun readServers(): Items<Server> {
    val operation = (ResourceAddress.root() op READ_RESOURCE_OPERATION) params {
        +ATTRIBUTES_ONLY
        +INCLUDE_RUNTIME
    }
    return Items(serverId).addAll(dmr(operation).asPropertyList().map { Server(it.value[RESULT]) })
//    if (it is FetchException && it.statusCode == 404.toShort()) {
//        emit(ModelNode()) // will end up in an empty server list
//    } else {
//        console.error("Unable to execute $operation: ${it.message}")
//    }
}

/*
    val serverEvent = handleAndOffer<Pair<String, String>, List<Server>> { _, event ->
        val (action, server) = event
        when (action) {
            "ADDED" -> "Added server $server"
            "REMOVED" -> "Removed server $server"
            else -> null
        }?.let { Notification.info(it) }
        readServers()
    }
*/

class Server(node: ModelNode) : NamedNode(node)

val serverId: IdProvider<Server, String> = { Id.build("srv", it.name) }

@OptIn(InternalCoroutinesApi::class)
class ServerPresenter : Presenter<ServerView> {

    override val view = ServerView()

    override fun show() {
        view.drawer?.let {
            cdi().serverStore.data.map { items -> items.selected.isNotEmpty() } handledBy it.expanded.update
        }
        MainScope().launch {
            action(readServers()) handledBy cdi().serverStore.update
        }
    }
}

class ServerView : View {
    internal var drawer: Drawer? = null

    override val elements = elements {
        pfSection("light".modifier(), "fill".modifier()) {
            classMap = cdi().serverStore.data
                .map { servers -> mapOf("display-none".util() to servers.all.isNotEmpty()) }
            pfEmptyState("server".fas(), "No Servers") {
                pfEmptyStateBody {
                    p {
                        +"No servers found. Please manage your servers in OpenShift using the WildFly operator."
                    }
                    p {
                        +"This view will update automatically, once there are servers available."
                    }
                }
                pfButton("primary".modifier()) {
                    +"Refresh"
                    clicks handledBy cdi().serverStore.refresh
                }
            }
        }

        pfSection("light".modifier()) {
            classMap = cdi().serverStore.data
                .map { servers -> mapOf("display-none".util() to servers.all.isEmpty()) }
            pfContent {
                h1 { +"Servers" }
                p { +"The list of servers managed by the WildFly operator." }
            }
        }
        pfSection("no-padding".modifier(), "padding-on-md".modifier()) {
            classMap = cdi().serverStore.data
                .map { servers -> mapOf("display-none".util() to servers.all.isEmpty()) }
            drawer = pfDrawer {
                pfDrawerMain {
                    pfDrawerContent {
                        domNode.style.background = "none"
                        pfDrawerBody {
                            pfDataList(cdi().serverStore) {
                                display = {
                                    pfDataListItem(it) {
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
                                                pfButton("primary".modifier()) {
                                                    +"Restart"
                                                    clicks.stopPropagation().map {
                                                        Notification(Severity.INFO, "Restart not yet implemented")
                                                    } handledBy Notification.store.add
                                                }
                                                pfButton("secondary".modifier()) {
                                                    +"Suspend"
                                                    clicks.stopPropagation().map {
                                                        Notification(Severity.INFO, "Suspend not yet implemented")
                                                    } handledBy Notification.store.add
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    val selection = cdi().serverStore.selection
                    selection.map { it.first() }.mapNotNull {
                        pfDrawerPanel {
                            pfDrawerBody {
                                pfDrawerHead {
                                    pfTitle(size = Size.LG) { +it.name }
                                    pfDrawerActions {
                                        pfDrawerClose()
                                    }
                                }
                            }
                            pfDrawerBody {
                                pfContent {
                                    dl {
                                        dt { +"Product Name" }
                                        dd { +it[PRODUCT_NAME].asString() }
                                        dt { +"Product Version" }
                                        dd { +it[PRODUCT_VERSION].asString() }
                                        dt { +"Launch Type" }
                                        dd { +it[LAUNCH_TYPE].asString() }
                                        dt { +"UUID" }
                                        dd { +it["uuid"].asString() }
                                    }
                                }
                            }
                        }
                    }.bind()
                }
            }
        }
    }
}

