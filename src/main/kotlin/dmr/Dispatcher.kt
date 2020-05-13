package org.wildfly.halos.dmr

import kotlinx.coroutines.await
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestMode
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import kotlin.browser.window

object Dispatcher {

    suspend fun execute(operation: Operation): ModelNode {
        val request = RequestInit().apply {
            method = "POST"
            headers = Headers().apply {
                append("Accept", "application/dmr-encoded")
                append("Content-Type", "application/dmr-encoded")
            }
            body = operation.toBase64()
            if (Environment.cors) {
                mode = "cors".asDynamic().unsafeCast<RequestMode>()
            }
        }

        val payload = window.fetch(Endpoint.management, request)
            .await()
            .text()
            .await()
        return ModelNode.fromBase64(payload)
    }
}
