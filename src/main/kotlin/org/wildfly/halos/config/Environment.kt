package org.wildfly.halos.config

object Environment {
    val version: Version = Version.parse("0.0.1") //Version.parse(process.env.HALOS_VERSION.unsafeCast<String>())
    val cors: Boolean = true //process.env.HALOS_CORS.unsafeCast<Boolean>()
    val proxyUrl: String = "http://localhost:8080" //process.env.HALOS_PROXY_URL.unsafeCast<String>()
    val restVersion: String = "v1" //process.env.HALOS_REST_VERSION.unsafeCast<String>()
}
