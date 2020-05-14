package org.wildfly.dmr

import kotlinx.coroutines.await
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestMode
import kotlin.browser.window

class Dispatcher(private val endpoint: String, private val cors: Boolean = true) {

    suspend fun execute(operation: Operation): ModelNode {
        val request = RequestInit().apply {
            method = "POST"
            headers = Headers().apply {
                append("Accept", "application/dmr-encoded")
                append("Content-Type", "application/dmr-encoded")
            }
            body = operation.toBase64()
            if (cors) {
                mode = "cors".asDynamic().unsafeCast<RequestMode>()
            }
        }

        val payload = window.fetch(endpoint, request)
            .await()
            .text()
            .await()
        return ModelNode.fromBase64(payload)
    }
}
