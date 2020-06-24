package org.wildfly.halos.config

object Endpoint {
    val instance: String = "${Environment.proxyUrl}/${Environment.restVersion}/instance"
    val management: String = "${Environment.proxyUrl}/${Environment.restVersion}/management"
}
