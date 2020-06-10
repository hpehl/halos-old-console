package org.wildfly.halos.config

import mu.KotlinLoggingLevel

object Environment {
    val version: Version = Version.parse(failSafe(process.env.HALOS_VERSION) { "0.0.0" })
    val cors: Boolean = failSafe(process.env.HALOS_CORS) { true }
    val proxyUrl: String = failSafe(process.env.HALOS_PROXY_URL) { "" }
    val restVersion: String = failSafe(process.env.HALOS_REST_VERSION) { "v1" }
    val logLevel: KotlinLoggingLevel = KotlinLoggingLevel.valueOf(failSafe(process.env.HALOS_LOG_LEVEL) { "INFO" })

    private fun <T> failSafe(value: dynamic, default: () -> T): T = if (value == undefined) {
        default()
    } else {
        value.unsafeCast<T>()
    }
}
