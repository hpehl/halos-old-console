package org.wildfly.halos.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import org.jboss.dmr.ModelDescriptionConstants
import org.jboss.dmr.op
import org.jboss.dmr.params
import org.patternfly.*
import org.wildfly.halos.cdi
import org.jboss.mvp.HasPresenter
import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.jboss.mvp.bind
import kotlin.browser.document

val servers = listOf(
    Server("server-0", "Server 1", "running"),
    Server("server-1", "Server 2", "suspending")
)

data class Server(val username: String, val name: String, val status: String)

class ServerPresenter : Presenter<ServerView> {

    private val dataProvider = DataProvider<Server> { Id.build("item", it.username) }
    override val token = TOKEN
    override val view = ServerView()

    override fun show() {
        dataProvider.bind(document.querySelector("#servers").pfDataList(dataProvider))
        dataProvider.update(servers)
    }

    internal fun doIt() {
        console.log("Called from view click handler.")
    }

    companion object {
        const val TOKEN = "server"
    }
}

class ServerView : View, HasPresenter<ServerPresenter, ServerView> {

    override val presenter: ServerPresenter by bind(ServerPresenter.TOKEN)

    override val elements = arrayOf(
        document.create.pfSection("light".modifier()) {
            pfContent {
                h1 {
                    classes += "pf-c-title"
                    +"halOS"
                }
                p { +"WildFly management console for OpenShift." }
                p {
                    +"Execute an "
                    pfLinkButton(text = "operation", inline = true) {
                        onClickFunction = { presenter.doIt() }
                    }
                    +"."
                }
            }
        },
        document.create.pfSection {
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
    )
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
