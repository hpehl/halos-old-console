package org.wildfly.halos.server

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import org.jboss.dmr.ModelDescriptionConstants
import org.jboss.dmr.op
import org.jboss.dmr.params
import org.jboss.mvp.HasPresenter
import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.jboss.mvp.bind
import org.patternfly.*
import org.w3c.dom.EventSource
import org.w3c.dom.EventSourceInit
import org.wildfly.halos.cdi
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import styled.StyledComponents.css
import kotlin.browser.document

val servers = listOf(
    Server("server-0", "Server 1", "running"),
    Server("server-1", "Server 2", "suspending")
)

data class Server(val username: String, val name: String, val status: String)

class ServerPresenter : Presenter<ServerView> {

    override val token = TOKEN
    override val view = ServerView()
    private val dataProvider = DataProvider<Server> { Id.build("item", it.username) }

    override fun bind() {
        val eventSource = EventSource(Endpoint.instance + "/subscribe", EventSourceInit(Environment.cors))
        eventSource.onmessage = {
            console.log("Message event from ${it.origin}: ${it.data}")
        }
    }

    override fun show() {
        val dropdown = document.querySelector("#test-dropdown").pfDropdown<String>()
        val dataList = document.querySelector("#servers").pfDataList(dataProvider)

        dropdown.addAll(listOf("One", "Two", "Three"))
        dataProvider.bind(dataList)
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
            pfDropdown<String>("Test Dropdown") {
                id = "test-dropdown"
                renderer = {
                    {
                        style = "background-color: #c99"
                        span {
                            +it.toUpperCase()
                        }
                    }
                }
            }
        },
        document.create.pfSection {
            pfDataList<Server> {
                id = "servers"
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
