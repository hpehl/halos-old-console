package org.wildfly.halos.config

external val process: Process

external interface Process {
    val env: dynamic
}
