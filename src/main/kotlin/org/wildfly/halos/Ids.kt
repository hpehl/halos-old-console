package org.wildfly.halos

import org.jboss.elemento.Id

object Ids {
    const val MAIN = "halos-main"
    const val SERVER_DROPDOWN = "server-dd"

    fun server(name: String): String = Id.build("srv", name)
}
