package org.wildfly.halos.meta

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.wildfly.halos.dmr
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.ACCESS_CONTROL
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.COMBINED_DESCRIPTIONS
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.OPERATIONS
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.READ_RESOURCE_DESCRIPTION_OPERATION
import org.wildfly.halos.dmr.ModelDescriptionConstants.Companion.TRIM_DESCRIPTIONS
import org.wildfly.halos.dmr.ModelNode
import org.wildfly.halos.dmr.ModelType
import org.wildfly.halos.dmr.Operation
import org.wildfly.halos.dmr.ResourceAddress
import org.wildfly.halos.dmr.op
import org.wildfly.halos.dmr.params

data class Metadata(
    val template: AddressTemplate = AddressTemplate.ROOT,
    val description: ResourceDescription = ResourceDescription(),
    val securityContext: SecurityContext = SecurityContext()
)

enum class Scope(val prefix: String) {
    NORMAL(""),
    RECURSIVE("recursive:/"),
    OPTIONAL("optional:/"),
    OPTIONAL_RECURSIVE("or:/");

    open fun optional(): Boolean = this == OPTIONAL || this == OPTIONAL_RECURSIVE
    open fun recursive(): Boolean = this == RECURSIVE || this == OPTIONAL_RECURSIVE
}

typealias MetadataRequest = Map<AddressTemplate, Scope>
typealias MetadataResult = Map<AddressTemplate, Metadata>

class MetadataRequestBuilder {
    internal val request: MutableMap<AddressTemplate, Scope> = mutableMapOf()

    operator fun String.unaryPlus() {
        request[AddressTemplate(this)] = Scope.NORMAL
    }

    operator fun AddressTemplate.unaryPlus() {
        request[this] = Scope.NORMAL
    }

    operator fun Pair<String, Scope>.unaryPlus() {
        request[AddressTemplate(this.first)] = this.second
    }

    operator fun Pair<AddressTemplate, Scope>.unaryPlus() {
        request[this.first] = this.second
    }
}

class MetadataException(message: String) : Throwable(message)

fun metadata(template: String, scope: Scope = Scope.NORMAL, consumer: (Metadata) -> Unit) {
    metadata(AddressTemplate(template), scope, consumer)
}

fun metadata(template: AddressTemplate, scope: Scope = Scope.NORMAL, consumer: (Metadata) -> Unit) {
    MainScope().launch {
        consumer(MetadataRegistry.find(template, scope))
    }
}

fun metadata(block: MetadataRequestBuilder.() -> Unit, consumer: (MetadataResult) -> Unit) {
    MainScope().launch {
        val builder = MetadataRequestBuilder()
        block(builder)
        consumer(MetadataRegistry.find(builder.request))
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun test() {
    metadata("subsystem=io") { metadata ->
        println(metadata)
    }
    metadata(AddressTemplate("subsystem=io")) { metadata ->
        println(metadata)
    }
    metadata({
        +"subsystem=io"
        +("subsystem=io" to Scope.RECURSIVE)
        +AddressTemplate.ROOT
        +(AddressTemplate("subsystem=io") to Scope.OPTIONAL)
    }) { result ->
        println(result)
    }
}

object MetadataRegistry {

    private val resourceDescriptions: MutableMap<ResourceAddress, ResourceDescription> = mutableMapOf()
    private val securityContexts: MutableMap<ResourceAddress, SecurityContext> = mutableMapOf()

    suspend fun find(request: MetadataRequest): MetadataResult {
        val result = failSafeGet(request)
        return if (allPresent(request, result)) {
            result
        } else {
            emptyMap()
        }
    }

    suspend fun find(template: AddressTemplate, scope: Scope = Scope.NORMAL): Metadata {
        val metadata = failSafeGet(template)
        return if (allPresent(metadata, scope)) {
            metadata
        } else {
            val operations = rrdOperations(template, metadata, scope)
            if (operations.isEmpty()) {
                throw MetadataException("Unable to create r-r-d operation for $scope$template")
            } else if (operations.size == 1) {
                val modelNode = dmr(operations[0])
                parseRrd(operations[0].address, modelNode, scope.recursive())
            } else {

            }
            Metadata()
        }
    }

    private fun failSafeGet(request: MetadataRequest): MetadataResult =
        request.map { (template, _) ->
            template to failSafeGet(template)
        }.toMap()


    private fun failSafeGet(template: AddressTemplate): Metadata {
        // TODO Use different resolvers?
        val rdAddress = template.resolve(WILDCARD_LAST_RESOLVER)
        val scAddress = template.resolve(WILDCARD_LAST_RESOLVER)
        val resourceDescription = resourceDescriptions[rdAddress] ?: ResourceDescription()
        val securityContext = securityContexts[scAddress] ?: SecurityContext()
        return Metadata(template, resourceDescription, securityContext)
    }

    private fun allPresent(request: MetadataRequest, result: MetadataResult): Boolean {
        for ((template, scope) in request) {
            val metadata: Metadata = result[template] ?: Metadata()
            if (!allPresent(metadata, scope)) {
                return false
            }
        }
        return true
    }

    private fun allPresent(metadata: Metadata, scope: Scope): Boolean {
        val defined = metadata.description.defined() && metadata.securityContext.defined()
        return if (scope.recursive()) {
            defined && metadata.description.recursive && metadata.securityContext.recursive
        } else defined
    }

    private fun rrdOperations(template: AddressTemplate, metadata: Metadata, scope: Scope): List<Operation> {
        val operations = mutableListOf<Operation>()
        if (metadata.description.undefined() && metadata.securityContext.undefined()) {
            val rdAddress = template.resolve(WILDCARD_LAST_RESOLVER)
            val scAddress = template.resolve(WILDCARD_LAST_RESOLVER)
            if (rdAddress == scAddress) {
                operations.add(rdAddress op READ_RESOURCE_DESCRIPTION_OPERATION params {
                    +(ACCESS_CONTROL to COMBINED_DESCRIPTIONS)
                    +OPERATIONS
                })
            } else {
                operations.add(rdAddress op READ_RESOURCE_DESCRIPTION_OPERATION params {
                    +OPERATIONS
                })
                operations.add(scAddress op READ_RESOURCE_DESCRIPTION_OPERATION params {
                    +(ACCESS_CONTROL to TRIM_DESCRIPTIONS)
                    +OPERATIONS
                })
            }
        } else if (metadata.description.undefined()) {
            val address = template.resolve(WILDCARD_LAST_RESOLVER)
            operations.add(address op READ_RESOURCE_DESCRIPTION_OPERATION params {
                +OPERATIONS
            })
        } else if (metadata.securityContext.undefined()) {
            val address = template.resolve(WILDCARD_LAST_RESOLVER)
            operations.add(address op READ_RESOURCE_DESCRIPTION_OPERATION params {
                +(ACCESS_CONTROL to TRIM_DESCRIPTIONS)
                +OPERATIONS
            })
        }
        return operations
    }

    private fun parseRrd(address: ResourceAddress, modelNode: ModelNode, recursive: Boolean) {
        if (modelNode.type() == ModelType.LIST) {

        } else {

        }
    }

    private fun parse(address: ResourceAddress, modelNode: ModelNode, recursive: Boolean) {

    }
}