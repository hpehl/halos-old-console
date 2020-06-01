package org.wildfly.halos.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.dom.create
import kotlinx.html.id
import kotlinx.html.p
import kotlinx.html.span
import org.jboss.dmr.*
import org.jboss.dmr.ModelDescriptionConstants.Companion.ATTRIBUTES_ONLY
import org.jboss.dmr.ModelDescriptionConstants.Companion.INCLUDE_RUNTIME
import org.jboss.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.jboss.dmr.ModelDescriptionConstants.Companion.RESULT
import org.jboss.mvp.*
import org.patternfly.*
import org.w3c.dom.EventSource
import org.w3c.dom.EventSourceInit
import org.w3c.dom.MessageEvent
import org.wildfly.halos.Ids
import org.wildfly.halos.cdi
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import kotlin.browser.document

class Server(node: ModelNode) : NamedNode(node)

class ServerSubscription() {
    private val subscriptions: MutableList<(MessageEvent) -> Unit> = mutableListOf()

    init {
        val eventSource = EventSource("${Endpoint.instance}/subscribe", EventSourceInit(Environment.cors))
        eventSource.onmessage = {
            for (subscription in subscriptions) {
                subscription(it)
            }
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

    override val elements = with(document.create) {
        arrayOf(
            pfSection("light".modifier()) {
                pfContent {
                    pfTitle("Server")
                    p { +"The list of servers managed by the WildFly operator." }
                }
            },
            pfSection {
                pfDataList<Server> {
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
        )
    }
}

fun readResource() {
    GlobalScope.launch {
        val operation = (ResourceAddress.root() op READ_RESOURCE_OPERATION) params {
            +ATTRIBUTES_ONLY
            +INCLUDE_RUNTIME
        }
        val node = cdi().dispatcher.execute(operation)
        console.log(node.toString())
    }
}
