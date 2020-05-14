package org.wildfly.dmr

import kotlin.browser.window

open class ModelNode(internal var value: ModelValue<*> = ModelValue.UNDEFINED) {

    // ------------------------------------------------------ operators & get

    operator fun contains(name: String): Boolean = name in value

    operator fun get(name: String): ModelNode {
        if (!defined()) value = ObjectValue()
        return value[name]
    }

    fun get(vararg names: String): ModelNode {
        var current = this
        for (name in names) {
            current = current[name]
        }
        return current
    }

    fun defined(): Boolean = value.type != ModelType.UNDEFINED

    fun size(): Int = asList().size

    // ------------------------------------------------------ as methods

    fun asBoolean(defaultValue: Boolean = false): Boolean = das(defaultValue) { value.asBoolean() }
    fun asBytes(): ByteArray = value.asBytes()
    fun asDouble(defaultValue: Double = 0.0): Double = das(defaultValue) { value.asDouble() }
    fun asInt(defaultValue: Int = 0): Int = das(defaultValue) { value.asInt() }
    fun asList(): List<ModelNode> = value.asList()
    fun asLong(defaultValue: Long = 0L): Long = das(defaultValue) { value.asLong() }
    fun asProperty(): Property = value.asProperty()
    fun asPropertyList(): List<Property> = value.asPropertyList()
    fun asObject(): ModelNode = value.asObject()
    fun asString(): String = value.asString()
    private fun <T> das(defaultValue: T, asFn: () -> T): T = if (!defined()) defaultValue else asFn.invoke()

    // ------------------------------------------------------ add

    fun add(): ModelNode {
        if (!defined()) {
            value = ListValue()
        }
        return value.add()
    }

    fun add(value: Any): ModelNode = add().apply {
        set(value)
    }

    fun add(name: String, value: Any): ModelNode = add().apply {
        set(name, value)
    }

    fun addEmptyList(): ModelNode = add().apply {
        setEmptyList()
    }

    fun addEmptyObject(): ModelNode = add().apply {
        setEmptyObject()
    }

    // ------------------------------------------------------ set et al

    fun set(value: Any) {
        this.value = when (value) {
            is Boolean -> BooleanValue(value)
            is ByteArray -> BytesValue(value)
            is Double -> DoubleValue(value)
            is Int -> IntValue(value)
            is Long -> LongValue(value)
            is ModelNode -> value.value
            is ModelType -> TypeValue(value)
            is Property -> PropertyValue(value)
            is String -> StringValue(value)
            else -> ModelValue.UNDEFINED
        }
    }

    fun set(value: List<ModelNode>) {
        this.value = ListValue(value.toMutableList())
    }

    fun set(name: String, value: Any) {
        val nodeValue = ModelNode().apply { set(value) }
        this.value = PropertyValue(name, nodeValue)
    }

    fun setEmptyList() {
        value = ListValue()
    }

    fun setEmptyObject() {
        value = ObjectValue()
    }

    fun remove(name: String) {
        value.remove(name)
    }

    fun clear() {
        value = ModelValue.UNDEFINED
    }

    // ------------------------------------------------------ equals, hashCode, toString

    override fun equals(other: Any?): Boolean = if (other is ModelNode) {
        value == other.value
    } else {
        false
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()

    fun format(builder: Appendable, indent: Int, multiLine: Boolean = true) {
        value.format(builder, indent, multiLine)
    }

    // ------------------------------------------------------ io

    fun write(out: DataOutput) {
        out.writeByte(value.type.typeChar.toInt())
        value.write(out)
    }

    fun read(input: DataInput) {
        val t = ModelType.forChar((input.readByte() and 0xFF).toChar())
        value = when (t) {
            ModelType.BIG_DECIMAL, ModelType.BIG_INTEGER -> {
                console.error("ModelType $t not supported. Fall back to undefined.")
                ModelValue.UNDEFINED
            }
            ModelType.BOOLEAN -> BooleanValue(input.readBoolean())
            ModelType.BYTES -> {
                val bytes = ByteArray(input.readInt())
                input.readBytes(bytes)
                BytesValue(bytes)
            }
            ModelType.DOUBLE -> DoubleValue(input.readDouble())
            ModelType.EXPRESSION -> ExpressionValue(input.readUTF())
            ModelType.INT -> IntValue(input.readInt())
            ModelType.LIST -> {
                val list = mutableListOf<ModelNode>()
                val size = input.readInt()
                for (i in 0 until size) {
                    list.add(ModelNode().apply { read(input) })
                }
                ListValue(list)
            }
            ModelType.LONG -> LongValue(input.readLong())
            ModelType.OBJECT -> {
                val map = mutableMapOf<String, ModelNode>()
                val size = input.readInt()
                for (i in 0 until size) {
                    val name = input.readUTF()
                    val value = ModelNode().apply { read(input) }
                    map[name] = value
                }
                ObjectValue(map)
            }
            ModelType.PROPERTY -> {
                val name = input.readUTF()
                val value = ModelNode().apply { read(input) }
                PropertyValue(Property(name, value))
            }
            ModelType.STRING -> StringValue(input.readUTF())
            ModelType.TYPE -> TypeValue(ModelType.forChar((input.readByte() and 0xFF).toChar()))
            ModelType.UNDEFINED -> ModelValue.UNDEFINED
        }
    }

    fun toBase64(): String {
        val out = DataOutput()
        write(out)
        return window.btoa(out.toString())
    }

    // ------------------------------------------------------ companion

    companion object {
        val UNDEFINED = ModelNode()

        fun fromBase64(encoded: String): ModelNode {
            val node = ModelNode()
            val decoded = window.atob(encoded)
            node.read(DataInput(stringToBytes(decoded)))
            return node
        }
    }
}