package org.wildfly.halos.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.dom.create
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.p
import kotlinx.html.span
import org.jboss.dmr.ModelDescriptionConstants.Companion.ATTRIBUTES_ONLY
import org.jboss.dmr.ModelDescriptionConstants.Companion.INCLUDE_RUNTIME
import org.jboss.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.jboss.dmr.ModelDescriptionConstants.Companion.RESULT
import org.jboss.dmr.ModelNode
import org.jboss.dmr.NamedNode
import org.jboss.dmr.ResourceAddress
import org.jboss.dmr.op
import org.jboss.dmr.params
import org.jboss.mvp.HasPresenter
import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.jboss.mvp.component
import org.jboss.mvp.token
import org.patternfly.ComponentType.DataList
import org.patternfly.ComponentType.Drawer
import org.patternfly.ComponentType.EmptyState
import org.patternfly.DataListComponent
import org.patternfly.DataProvider
import org.patternfly.DividerVariant
import org.patternfly.DrawerComponent
import org.patternfly.EmptyStateComponent
import org.patternfly.Notification
import org.patternfly.SelectionMode
import org.patternfly.Style
import org.patternfly.charts.pfcDonut
import org.patternfly.fas
import org.patternfly.hide
import org.patternfly.modifier
import org.patternfly.pfButton
import org.patternfly.pfCell
import org.patternfly.pfContent
import org.patternfly.pfDataList
import org.patternfly.pfDivider
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
import org.patternfly.pfItemContent
import org.patternfly.pfItemRow
import org.patternfly.pfSection
import org.patternfly.pfTitle
import org.patternfly.selector
import org.patternfly.visible
import org.w3c.dom.MessageEvent
import org.wildfly.halos.Ids
import org.wildfly.halos.cdi
import react.dom.render
import kotlin.browser.document

class Server(node: ModelNode) : NamedNode(node)

data class Servers(val servers: List<Server>)

data class ServerUpdate(val event: MessageEvent)

class ServerPresenter : Presenter<ServerView> {

    override val token = TOKEN
    override val view = ServerView()
    private val dispatcher = cdi().dispatcher
    private val eventBus = cdi().eventBus

    override fun bind() {
        eventBus.subscribe(ServerUpdate::class) {
            Notification.info("${it.event.origin}: ${it.event.data}")
            updateServer()
        }
        view.init()
    }

    override fun show() {
        updateServer()
    }

    internal fun updateServer() {
        GlobalScope.launch {
            val operation = (ResourceAddress.root() op READ_RESOURCE_OPERATION) params {
                +ATTRIBUTES_ONLY
                +INCLUDE_RUNTIME
            }
            val node = dispatcher.execute(operation)
            view.update(Servers(node.asPropertyList().map { Server(it.value[RESULT]) }))
        }
    }

    companion object {
        const val TOKEN = "server"
    }
}

class ServerView : View, HasPresenter<ServerPresenter> {

    override val presenter: ServerPresenter by token(ServerPresenter.TOKEN)
    override val elements = with(document.create) {
        arrayOf(
            pfSection("light".modifier()) {
                pfContent {
                    pfTitle("Server")
                    p { +"The list of servers managed by the WildFly operator." }
                }
            },
            pfDivider(DividerVariant.DIV),
            pfSection("light".modifier(), "no-padding".modifier()) {
                pfDrawer {
                    pfDrawerMain {
                        pfDrawerContent {
                            pfDrawerBody {
                                pfEmptyState("server".fas(), "No Servers") {
                                    pfEmptyStateBody {
                                        +"No servers found. Lorem ipsum..."
                                    }
                                    pfButton(Style.primary, "Refresh") {
                                        onClickFunction = { presenter.updateServer() }
                                    }
                                }
                                pfDataList<Server>(SelectionMode.SINGLE) {
                                    renderer = { server, dataProvider ->
                                        {
                                            pfItemRow {
                                                pfItemContent {
                                                    pfCell {
                                                        span {
                                                            id = dataProvider.identifier(server)
                                                            +server.name
                                                        }
                                                    }
                                                    pfCell { +server["release-version"].asString() }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        pfDrawerPanel()
                    }
                }
            }
        )
    }

    private val drawer: DrawerComponent by component(Drawer.selector()) { it.pfDrawer() }
    private val dataProvider = DataProvider<Server> { Ids.server(it.name) }
    private val dataList: DataListComponent<Server> by component(DataList.selector()) {
        it.pfDataList(dataProvider)
    }
    private val emptyState: EmptyStateComponent by component(EmptyState.selector()) { it.pfEmptyState() }

    internal fun init() {
        dataProvider.bind(dataList)
        dataProvider.onSelect { show(it) }
        emptyState.hide()
        dataList.hide()
    }

    internal fun update(servers: Servers) {
        with(servers.servers) {
            emptyState.visible = isEmpty()
            dataList.visible = isNotEmpty()
            if (isNotEmpty()) {
                dataProvider.update(this)
            }
        }
    }

    private fun show(server: Server) {
        drawer.show {
            pfDrawerBody {
                pfDrawerHead {
                    pfTitle(server.name)
                    pfDrawerActions {
                        pfDrawerClose()
                    }
                }
            }
            pfDrawerBody {
                p { +server["release-version"].asString() }
            }
            pfDrawerBody {
                id = "server-chart"
            }
        }
        render(document.getElementById("server-chart")) {
            pfcDonut {
                attrs {
                    constrainToVisibleArea = true
                    data = js("""[{ x: 'Cats', y: 35 }, { x: 'Dogs', y: 55 }, { x: 'Birds', y: 10 }]""")
                    subTitle = server.name
                    title = "100"
                }
            }
        }
    }
}
