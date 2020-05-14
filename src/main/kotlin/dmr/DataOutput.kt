package org.wildfly.halos.dmr

import mu.KotlinLogging

private val logger = KotlinLogging.logger("dmr")

class DataOutput {

    private var pos: Int = 0
    private var bytes: ByteArray = ByteArray(256)

    fun bytes(): ByteArray = bytes.copyOfRange(0, pos)

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

    fun writeDouble(v: Double) {
        writeLong(v.toRawBits())
    }

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
        var bp = 0
        val b = ByteArray(s.length * 3)
        for (c in s) {
            when {
                c.toInt() in 1..0x7f -> {
                    b[bp++] = c.toByte()
                }
                c.toInt() <= 0x07ff -> {
                    b[bp++] = (0xc0 or 0x1f and c.toInt() shr 6).toByte()
                    b[bp++] = (0x80 or 0x3f and c.toInt()).toByte()
                }
                else -> {
                    b[bp++] = (0xe0 or 0x0f and c.toInt() shr 12).toByte()
                    b[bp++] = (0x80 or 0x3f and c.toInt() shr 6).toByte()
                    b[bp++] = (0x80 or 0x3f and c.toInt()).toByte()
                }
            }
        }
        writeShort(bp)
        for (i in 0 until bp) {
            bytes[pos++] = b[i]
        }
    }

    private fun growToFit(size: Int) {
        if (pos + size >= bytes.size) {
            bytes = bytes.copyInto(ByteArray(bytes.size + size))
        }
    }

    override fun toString(): String = jsString(bytes())

    private fun jsString(buffer: ByteArray): String = js(
        "var s='';var b=new Uint8Array(buffer);var l=b.byteLength;for(var i=0;i<l;i++){s+=String.fromCharCode(b[i]);}return s;"
    ) as String
}