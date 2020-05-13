package org.wildfly.halos.dmr

class DataOutput {

    private var pos: Int = 0
    private var bytes: ByteArray = ByteArray(256)

    fun bytes(): ByteArray = bytes.copyOfRange(0, pos)

    override fun toString(): String = bytes().toString()

    fun writeBoolean(v: Boolean) {
        growToFit(1)
        bytes[pos++] = if (v) 1.toByte() else 0.toByte()
    }

    fun writeByte(v: Int) {
        growToFit(1)
        bytes[pos++] = v.toByte()
    }

    fun writeBytes(b: ByteArray) {
        growToFit(b.size)
        b.copyInto(bytes, pos)
        pos += b.size
    }

    fun writeDouble(v: Double) = writeLong(v.toRawBits())

    fun writeInt(v: Int) {
        growToFit(4)
        bytes[pos++] = (v ushr 24).toByte()
        bytes[pos++] = (v ushr 16 and 0xFF).toByte()
        bytes[pos++] = (v ushr 8 and 0xFF).toByte()
        bytes[pos++] = (v and 0xFF).toByte()
    }

    fun writeLong(v: Long) {
        growToFit(8)
        bytes[pos++] = (v ushr 56).toByte()
        bytes[pos++] = (v ushr 48 and 0xFF).toByte()
        bytes[pos++] = (v ushr 40 and 0xFF).toByte()
        bytes[pos++] = (v ushr 32 and 0xFF).toByte()
        bytes[pos++] = (v ushr 24 and 0xFF).toByte()
        bytes[pos++] = (v ushr 16 and 0xFF).toByte()
        bytes[pos++] = (v ushr 8 and 0xFF).toByte()
        bytes[pos++] = (v and 0xFF).toByte()
    }

    fun writeShort(v: Int) {
        growToFit(2)
        bytes[pos++] = (v ushr 8).toByte()
        bytes[pos++] = (v and 0xFF).toByte()
    }

    fun writeUTF(s: String) {
        var i = 0
        val b = ByteArray(s.length * 3)
        for (c in s) {
            when {
                c.toInt() in 1..0x7f -> {
                    b[i++] = c.toByte()
                }
                c.toInt() <= 0x07ff -> {
                    b[i++] = (0xc0 or 0x1f and c.toInt() shr 6).toByte()
                    b[i++] = (0x80 or 0x3f and c.toInt()).toByte()
                }
                else -> {
                    b[i++] = (0xe0 or 0x0f and c.toInt() shr 12).toByte()
                    b[i++] = (0x80 or 0x3f and c.toInt() shr 6).toByte()
                    b[i++] = (0x80 or 0x3f and c.toInt()).toByte()
                }
            }
        }
        writeShort(i)
        writeBytes(b)
    }

    private fun growToFit(size: Int) {
        if (pos + size >= bytes.size) {
            bytes = bytes.copyInto(ByteArray(bytes.size + size))
        }
    }
}