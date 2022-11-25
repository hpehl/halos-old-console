package org.wildfly.halos

import dev.fritz2.remote.Request
import dev.fritz2.remote.getBody
import org.w3c.fetch.CORS
import org.w3c.fetch.RequestMode
import org.wildfly.halos.config.Endpoint
import org.wildfly.halos.config.Environment
import org.wildfly.halos.dmr.ModelNode
import org.wildfly.halos.dmr.Operation

suspend fun dmr(operation: Operation): ModelNode {
    val request = if (Environment.cors) {
        Request(Endpoint.management, mode = RequestMode.CORS)
    } else {
        Request(Endpoint.management)
    }
    val body = request.accept("application/dmr-encoded")
        .contentType("application/dmr-encoded")
        .body(operation.toBase64())
        .post()
        .getBody()
    return ModelNode.fromBase64(body)
}
