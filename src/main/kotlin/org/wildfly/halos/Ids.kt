package org.wildfly.halos

import org.patternfly.Id

object Ids {
    const val MAIN = "halos-main"
    const val SERVER_LIST = "server-dl"
    const val SERVER_DROPDOWN = "server-dd"

    fun server(name: String): String = Id.build("srv", name)
}