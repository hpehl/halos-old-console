package org.wildfly.halos.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.jboss.dmr.ModelDescriptionConstants
import org.jboss.dmr.op
import org.jboss.dmr.params
import org.patternfly.*
import org.w3c.dom.HTMLElement
import org.wildfly.halos.cdi
import org.wildfly.halos.mvp.Presenter
import org.wildfly.halos.mvp.View
import kotlin.browser.document

val servers = listOf(
    Server("server-0", "Server 1", "running"),
    Server("server-1", "Server 2", "suspending")
)

data class Server(val username: String, val name: String, val status: String)

class ServerPresenter : Presenter<ServerView> {

    override val token = "server"
    override val view = ServerView()
    private val dataProvider = DataProvider<Server> { Id.build("item", it.username) }

    override fun show() {
        dataProvider.bind(document.querySelector("#servers").pfDataList(dataProvider))
        dataProvider.update(servers)
    }
}

class ServerView : View {

    override val elements: TagConsumer<HTMLElement>.() -> Unit = {
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
            pfDataList<Server>("servers") {
                renderer = { user, dataProvider ->
                    {
                        pfItemRow {
                            pfItemContent {
                                pfCell {
                                    span {
                                        id = dataProvider.identifier(user)
                                        +user.username
                                    }
                                }
                                pfCell { +user.status }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun readResource() {
    GlobalScope.launch {
        val operation = ("subsystem=ee" op ModelDescriptionConstants.READ_RESOURCE_OPERATION) params {
            +ModelDescriptionConstants.INCLUDE_RUNTIME
            +(ModelDescriptionConstants.RECURSIVE_DEPTH to 1)
        }
        val node = cdi().dispatcher.execute(operation)
        console.log(node.toString())
    }
}
