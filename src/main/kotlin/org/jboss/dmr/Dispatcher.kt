package org.jboss.dmr

import kotlinx.coroutines.await
import org.w3c.fetch.CORS
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
                mode = RequestMode.CORS
            }
        }

        return window.fetch(endpoint, request)
            .then {
                if (!it.ok) {
                    error("${it.status} ${it.statusText}")
                } else {
                    it.text()
                }
            }
            .then {
                ModelNode.fromBase64(it)
            }
            .catch {
                console.error("Unable to execute $operation: ${it.message ?: "unknown error"}")
                ModelNode()
            }
            .await()
    }
}
