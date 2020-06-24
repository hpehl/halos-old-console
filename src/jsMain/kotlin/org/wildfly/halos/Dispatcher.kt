package org.wildfly.halos

import dev.fritz2.remote.Request
import dev.fritz2.remote.onError
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jboss.dmr.ModelNode
import org.jboss.dmr.Operation
import org.w3c.fetch.CORS
import org.w3c.fetch.RequestMode
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment

fun dmr(operation: Operation): Flow<ModelNode> {
    val request = if (Environment.cors) {
        Request(Endpoint.management, mode = RequestMode.CORS)
    } else {
        Request(Endpoint.management)
    }
    return request.accept("application/dmr-encoded")
        .contentType("application/dmr-encoded")
        .body(operation.toBase64())
        .post()
        .onError { console.error("Unable to execute $operation: ${it.statusCode} ${it.body}") }
        .map { ModelNode.fromBase64(it.text().await()) }
}
