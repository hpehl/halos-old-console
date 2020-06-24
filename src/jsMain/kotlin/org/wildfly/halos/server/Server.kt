package org.wildfly.halos.server

import dev.fritz2.binding.handledBy
import dev.fritz2.lenses.WithId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jboss.dmr.ModelDescriptionConstants.Companion.ATTRIBUTES_ONLY
import org.jboss.dmr.ModelDescriptionConstants.Companion.INCLUDE_RUNTIME
import org.jboss.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_OPERATION
import org.jboss.dmr.ModelDescriptionConstants.Companion.RESULT
import org.jboss.dmr.ModelNode
import org.jboss.dmr.NamedNode
import org.jboss.dmr.ResourceAddress
import org.jboss.dmr.op
import org.jboss.dmr.params
import org.jboss.elemento.Id
import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.jboss.mvp.renderAll
import org.patternfly.DividerVariant
import org.patternfly.Notification
import org.patternfly.SelectionMode
import org.patternfly.modifier
import org.patternfly.pfContent
import org.patternfly.pfDataList
import org.patternfly.pfDataListItemCell
import org.patternfly.pfDataListItemContent
import org.patternfly.pfDataListItemRow
import org.patternfly.pfDivider
import org.patternfly.pfSection
import org.patternfly.pfTitle
import org.wildfly.halos.Places
import org.wildfly.halos.cdi
import org.wildfly.halos.dmr

fun refreshServer() {
    dmr((ResourceAddress.root() op READ_RESOURCE_OPERATION) params {
        +ATTRIBUTES_ONLY
        +INCLUDE_RUNTIME
    }).map { result ->
        result.asPropertyList().map { Server(it.value[RESULT]) }
    } handledBy cdi().serverStore.update
}

class Server(node: ModelNode) : NamedNode(node), WithId {
    override val id: String
        get() = name
}

class ServerPresenter : Presenter<ServerView> {

    override val token = Places.server
    override val view = ServerView()

    override fun show() {
        refreshServer()
        MainScope().launch {
            cdi().serverStore.selects.collect {
                Notification.info("Selected ${it.name}")
            }
        }
    }
}

class ServerView : View {
    override val elements = renderAll(
        {
            pfSection("light".modifier()) {
                pfContent {
                    pfTitle("Server")
                    p { text("The list of servers managed by the WildFly operator.") }
                }
            }
        },
        { pfDivider(DividerVariant.DIV) },
        {
            pfSection("light".modifier(), "no-padding".modifier()) {
                pfDataList(SelectionMode.SINGLE, cdi().serverStore) {
                    identifier = { Id.asId(it.name) }
                    display = {
                        {
                            pfDataListItemRow {
                                pfDataListItemContent {
                                    pfDataListItemCell {
                                        span {
                                            text(it.name)
                                        }
                                    }
                                    pfDataListItemCell { text(it["release-version"].asString()) }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
