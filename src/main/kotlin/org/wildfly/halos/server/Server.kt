package org.wildfly.halos.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.dom.create
import kotlinx.html.id
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
import org.patternfly.ComponentType.Drawer
import org.patternfly.DataListComponent
import org.patternfly.DataProvider
import org.patternfly.DividerVariant.DIV
import org.patternfly.DrawerComponent
import org.patternfly.Notification
import org.patternfly.SelectionMode
import org.patternfly.charts.pfcDonut
import org.patternfly.modifier
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
import org.patternfly.pfItemContent
import org.patternfly.pfItemRow
import org.patternfly.pfSection
import org.patternfly.pfTitle
import org.patternfly.selector
import org.w3c.dom.EventSource
import org.w3c.dom.EventSourceInit
import org.w3c.dom.MessageEvent
import org.wildfly.halos.Ids
import org.wildfly.halos.cdi
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import react.dom.render
import kotlin.browser.document

class Server(node: ModelNode) : NamedNode(node)

class ServerSubscription {
    private val subscriptions: MutableList<(MessageEvent) -> Unit> = mutableListOf()

    init {
        val eventSource = EventSource("${Endpoint.instance}/subscribe", EventSourceInit(Environment.cors))
        eventSource.onmessage = {
            subscriptions.forEach { sub -> sub(it) }
        }
    }

    fun subscribe(subscription: (MessageEvent) -> Unit) {
        subscriptions.add(subscription)
    }
}

class ServerPresenter : Presenter<ServerView> {

    override val token = TOKEN
    override val view = ServerView()
    internal val dataProvider = DataProvider<Server> { Ids.server(it.name) }
    private val serverSubscription = cdi().serverSubscription

    override fun bind() {
        dataProvider.bind(view.dataList)
        dataProvider.onSelect { view.show(it) }
        serverSubscription.subscribe {
            Notification.info("${it.origin}: ${it.data}")
            updateServer()
        }
    }

    override fun show() {
        updateServer()
    }

    private fun updateServer() {
        GlobalScope.launch {
            val operation = (ResourceAddress.root() op READ_RESOURCE_OPERATION) params {
                +ATTRIBUTES_ONLY
                +INCLUDE_RUNTIME
            }
            val node = cdi().dispatcher.execute(operation)
            val servers = node.asPropertyList().map { Server(it.value[RESULT]) }
            dataProvider.update(servers)
        }
    }

    companion object {
        const val TOKEN = "server"
    }
}

class ServerView : View, HasPresenter<ServerPresenter> {

    override val presenter: ServerPresenter by token(ServerPresenter.TOKEN)
    internal val dataList: DataListComponent<Server> by component("#${Ids.SERVER_LIST}") {
        it.pfDataList(presenter.dataProvider)
    }
    private val drawer: DrawerComponent by component(Drawer.selector()) { it.pfDrawer() }

    override val elements = with(document.create) {
        arrayOf(
            pfSection("light".modifier()) {
                pfContent {
                    pfTitle("Server")
                    p { +"The list of servers managed by the WildFly operator." }
                }
            },
            pfDivider(DIV),
            pfSection("light".modifier(), "no-padding".modifier()) {
                pfDrawer {
                    pfDrawerMain {
                        pfDrawerContent {
                            pfDrawerBody {
                                pfDataList<Server>(SelectionMode.SINGLE) {
                                    id = Ids.SERVER_LIST
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

    internal fun show(server: Server) {
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
