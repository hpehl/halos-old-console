package org.wildfly.halos.config

object Environment {
    val version: Version =
        Version.parse("0.0.1"/*System.getProperty("halos.version", "0.0.1")*/)
    val cors: Boolean = true //Boolean.parseBoolean(System.getProperty("halos.cors", "true"))
    val proxyUrl: String = "http://localhost:8080" //System.getProperty("halos.proxy.url", "http://localhost:8080")
    val restVersion: String = "v1" //System.getProperty("halos.rest.version", "v1")
}