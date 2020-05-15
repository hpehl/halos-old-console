package org.jboss.dmr

import kotlin.math.min

internal fun Appendable.appendQuoted(s: String): Appendable {
    append('"')
    for (c in s) {
        if (c == '"' || c == '\\') {
            append('\\').append(c)
        } else {
            append(c)
        }
    }
    append('"')
    return this
}

internal fun Appendable.indent(count: Int): Appendable {
    for (i in 0 until count) {
        append("  ")
    }
    return this
}

abstract class ModelValue<T>(val value: T, val type: ModelType) {

    open operator fun contains(name: String): Boolean = false
    open operator fun get(name: String): ModelNode = ModelNode.UNDEFINED

    open fun add(): ModelNode = ModelNode.UNDEFINED
    open fun remove(name: String) {}

    open fun asBoolean(): Boolean = false
    open fun asBytes(): ByteArray = ByteArray(0)
    open fun asDouble(): Double = 0.0
    open fun asInt(): Int = 0
    open fun asList(): List<ModelNode> = emptyList()
    open fun asLong(): Long = 0L
    open fun asObject(): ModelNode = ModelNode.UNDEFINED
    open fun asProperty(): Property = Property.UNDEFINED
    open fun asPropertyList(): List<Property> = emptyList()
    open fun asString(): String = value.toString()
    open fun asType(): ModelType = type

    open fun format(builder: Appendable, indent: Int, multiLine: Boolean = true) {
        builder.append(asString())
    }

    override fun equals(other: Any?): Boolean = if (other is ModelValue<*>) value == other.value else false
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = buildString {
        format(this, 0)
    }

    abstract fun write(out: DataOutput)

    companion object {
        val UNDEFINED: ModelValue<Unit> = object : ModelValue<Unit>(Unit, ModelType.UNDEFINED) {
            override fun asString(): String {
                return "undefined"
            }

            override fun write(out: DataOutput) = Unit
        }
    }
}

internal abstract class StringBasedValue(value: String, type: ModelType) : ModelValue<String>(value, type) {

    override fun asBoolean(): Boolean = value.toBoolean()

    @ExperimentalStdlibApi
    override fun asBytes(): ByteArray = value.encodeToByteArray()

    override fun asDouble(): Double = try {
        value.toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }

    override fun asInt(): Int = try {
        value.toInt()
    } catch (e: NumberFormatException) {
        0
    }

    override fun asLong(): Long = try {
        value.toLong()
    } catch (e: NumberFormatException) {
        0L
    }

    override fun write(out: DataOutput) = out.writeUTF(value)
}

// ------------------------------------------------------ implementations (a-z)

internal class BooleanValue(value: Boolean) : ModelValue<Boolean>(value, ModelType.BOOLEAN) {
    override fun asBytes(): ByteArray = if (value) byteArrayOf(1) else byteArrayOf(0)
    override fun asDouble(): Double = if (value) 1.0 else 0.0
    override fun asInt(): Int = if (value) 1 else 0
    override fun asLong(): Long = if (value) 1L else 0L

    override fun write(out: DataOutput) = out.writeBoolean(value)
}

internal class BytesValue(value: ByteArray) : ModelValue<ByteArray>(value, ModelType.BYTES) {
    override fun asBytes(): ByteArray = value

    override fun asInt(): Int {
        val length = value.size
        val cnt: Int = min(4, length)
        var v = 0
        for (i in 0 until cnt) {
            v = v shl 8
            v = v or (value[length - cnt + i].toInt() and 0xff)
        }
        return v
    }

    override fun asLong(): Long {
        val length = value.size
        val cnt: Int = min(8, length)
        var v = 0L
        for (i in 0 until cnt) {
            v = v shl 8
            v = v or (value[length - cnt + i].toLong() and 0xff)
        }
        return v
    }

    override fun asString(): String = buildString {
        format(this, 0, false)
    }

    override fun write(out: DataOutput) {
        out.writeInt(value.size)
        out.writeBytes(value)
    }
}

internal class DoubleValue(value: Double) : ModelValue<Double>(value, ModelType.DOUBLE) {
    override fun asBoolean(): Boolean = value != 0.0
    override fun asDouble(): Double = value
    override fun asInt(): Int = value.toInt()
    override fun asLong(): Long = value.toLong()

    override fun write(out: DataOutput) = out.writeDouble(value)
}

internal class ExpressionValue(value: String) : StringBasedValue(value, ModelType.EXPRESSION) {
    override fun format(builder: Appendable, indent: Int, multiLine: Boolean) {
        builder.append("expression ").appendQuoted(value)
        super.format(builder, indent, multiLine)
    }
}

internal class IntValue(value: Int) : ModelValue<Int>(value, ModelType.INT) {
    override fun asBoolean(): Boolean = value != 0

    override fun asBytes(): ByteArray {
        val bytes = ByteArray(4)
        bytes[0] = (value ushr 24).toByte()
        bytes[1] = (value ushr 16).toByte()
        bytes[2] = (value ushr 8).toByte()
        bytes[3] = value.toByte()
        return bytes
    }

    override fun asDouble(): Double = value.toDouble()
    override fun asInt(): Int = value
    override fun asLong(): Long = value.toLong()

    override fun write(out: DataOutput) = out.writeInt(value)
}

internal class ListValue(value: MutableList<ModelNode> = mutableListOf()) :
    ModelValue<MutableList<ModelNode>>(value, ModelType.LIST) {

    override fun add(): ModelNode {
        val node = ModelNode()
        value.add(node)
        return node
    }

    override fun asBoolean(): Boolean = value.isNotEmpty()
    override fun asDouble(): Double = value.size.toDouble()
    override fun asInt(): Int = value.size
    override fun asList(): List<ModelNode> = value
    override fun asLong(): Long = value.size.toLong()

    override fun asObject(): ModelNode {
        val result = ModelNode()
        val iterator: Iterator<ModelNode> = value.iterator()
        while (iterator.hasNext()) {
            val node = iterator.next()
            if (node.value.type == ModelType.PROPERTY) {
                val property = node.asProperty()
                result[property.name].set(property.value)
            } else if (iterator.hasNext()) {
                val name = node.asString()
                val value = iterator.next()
                result[name].set(value)
            }
        }
        return result
    }

    override fun asProperty(): Property =
        if (value.size == 2) {
            Property(value[0].asString(), value[1])
        } else {
            super.asProperty()
        }

    override fun asPropertyList(): List<Property> {
        val properties = mutableListOf<Property>()
        val iterator = value.iterator()
        while (iterator.hasNext()) {
            val node = iterator.next()
            if (node.value.type == ModelType.PROPERTY || node.value.type == ModelType.OBJECT) {
                properties.add(node.asProperty())
            } else if (iterator.hasNext()) {
                val name = node.asString()
                val value = iterator.next()
                properties.add(Property(name, value))
            } else {
                return super.asPropertyList()
            }
        }
        return properties.toList()
    }

    override fun asString(): String = buildString {
        format(this, 0, false)
    }

    override fun format(builder: Appendable, indent: Int, multiLine: Boolean) {
        builder.append('[')
        val ml = multiLine && value.size > 1
        if (ml) {
            builder.append('\n').indent(indent + 1)
        }
        val iterator = value.iterator()
        while (iterator.hasNext()) {
            val node = iterator.next()
            node.format(builder, indent + 1, multiLine)
            if (iterator.hasNext()) {
                if (ml) {
                    builder.append(",\n").indent(indent + 1)
                } else {
                    builder.append(',')
                }
            }
        }
        if (ml) {
            builder.append('\n').indent(indent)
        }
        builder.append(']')
    }

    override fun write(out: DataOutput) {
        out.writeInt(value.size)
        for (node in value) {
            node.write(out)
        }
    }
}

internal class LongValue(value: Long) : ModelValue<Long>(value, ModelType.LONG) {
    override fun asBoolean(): Boolean = value != 0L

    override fun asBytes(): ByteArray {
        val bytes = ByteArray(8)
        bytes[0] = (value ushr 56).toByte()
        bytes[1] = (value ushr 48).toByte()
        bytes[2] = (value ushr 40).toByte()
        bytes[3] = (value ushr 32).toByte()
        bytes[4] = (value ushr 24).toByte()
        bytes[5] = (value ushr 16).toByte()
        bytes[6] = (value ushr 8).toByte()
        bytes[7] = value.toByte()
        return bytes
    }

    override fun asDouble(): Double = value.toDouble()
    override fun asInt(): Int = value.toInt()
    override fun asLong(): Long = value

    override fun format(builder: Appendable, indent: Int, multiLine: Boolean) {
        builder.append(value.toString()).append('L')
    }

    override fun write(out: DataOutput) = out.writeLong(value)
}

internal class ObjectValue(value: MutableMap<String, ModelNode> = mutableMapOf()) :
    ModelValue<MutableMap<String, ModelNode>>(value, ModelType.OBJECT) {

    override fun contains(name: String): Boolean = value.contains(name)
    override fun get(name: String): ModelNode = value.getOrPut(name) { ModelNode() }
    override fun remove(name: String) {
        value.remove(name)
    }

    override fun asBoolean(): Boolean = value.isNotEmpty()
    override fun asDouble(): Double = value.size.toDouble()
    override fun asInt(): Int = value.size

    override fun asList(): List<ModelNode> {
        val nodes = mutableListOf<ModelNode>()
        for ((key, value) in value) {
            val node = ModelNode().apply {
                set(key, value)
            }
            nodes.add(node)
        }
        return nodes.toList()
    }

    override fun asLong(): Long = value.size.toLong()
    override fun asObject(): ModelNode =
        ModelNode(
            ObjectValue(value)
        )

    override fun asProperty(): Property = if (value.size == 1) {
        asPropertyList()[0]
    } else {
        super.asProperty()
    }

    override fun asPropertyList(): List<Property> = value.map {
        Property(it.key, it.value)
    }.toList()

    override fun asString(): String = buildString {
        format(this, 0, false)
    }

    override fun format(builder: Appendable, indent: Int, multiLine: Boolean) {
        builder.append('{')
        val ml = multiLine && value.size > 1
        if (ml) {
            builder.append('\n').indent(indent + 1)
        }
        val iterator = value.iterator()
        while (iterator.hasNext()) {
            val (name, value) = iterator.next()
            builder.appendQuoted(name).append(" => ")
            value.format(builder, indent + 1, multiLine)
            if (iterator.hasNext()) {
                if (ml) {
                    builder.append(",\n").indent(indent + 1)
                } else {
                    builder.append(',')
                }
            }
        }
        if (ml) {
            builder.append('\n').indent(indent)
        }
        builder.append('}')
    }

    override fun write(out: DataOutput) {
        out.writeInt(value.size)
        for ((name, value) in value) {
            out.writeUTF(name)
            value.write(out)
        }
    }
}

internal class PropertyValue(value: Property) : ModelValue<Property>(value, ModelType.PROPERTY) {
    constructor(name: String, value: ModelNode) : this(
        Property(name, value)
    )

    override fun contains(name: String): Boolean = value.name == name
    override fun get(name: String): ModelNode = if (value.name == name) value.value else super.get(name)
    override fun asObject(): ModelNode = ModelNode()
        .apply {
            val pv = this@PropertyValue
            this[pv.value.name].set(pv.value.value)
        }

    override fun asProperty(): Property = value
    override fun asPropertyList(): List<Property> = listOf(value)
    override fun asString(): String = buildString {
        "(${appendQuoted(value.name)} => ${value.value})"
    }

    override fun write(out: DataOutput) {
        out.writeUTF(value.name)
        value.value.write(out)
    }
}

internal class StringValue(value: String) : StringBasedValue(value, ModelType.STRING) {
    override fun asType(): ModelType = ModelType.valueOf(value)
    override fun format(builder: Appendable, indent: Int, multiLine: Boolean) {
        builder.appendQuoted(value)
    }
}

internal class TypeValue(value: ModelType) : ModelValue<ModelType>(value, ModelType.TYPE) {
    override fun asBoolean(): Boolean = value != ModelType.UNDEFINED
    override fun asString(): String = value.name
    override fun asType(): ModelType = value

    override fun write(out: DataOutput) {
        out.writeByte(value.typeChar.toInt())
    }
}
