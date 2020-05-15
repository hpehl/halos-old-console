package org.jboss.dmr

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.DataView
import org.khronos.webgl.Int8Array
import org.khronos.webgl.set

class DataInput(private val bytes: ByteArray) {

    private var pos: Int = 0

    fun readBoolean(): Boolean {
        return read() != 0
    }

    fun readByte(): Int = read()

    fun readBytes(b: ByteArray) {
        for (i in b.indices) {
            b[i] = bytes[pos++]
        }
    }

    fun readDouble(): Double {
        val doubleBytes = ByteArray(8)
        for (i in doubleBytes.indices) {
            doubleBytes[i] = bytes[pos++]
        }

        val buffer = ArrayBuffer(8)
        val array = Int8Array(buffer)
        val view = DataView(buffer)
        for (i in doubleBytes.indices) {
            array[i] = doubleBytes[i]
        }
        return view.getFloat64(0)
    }

    fun readInt(): Int {
        val a: Int = read()
        val b: Int = read()
        val c: Int = read()
        val d: Int = read()
        return a shl 24 or (b shl 16) or (c shl 8) or d
    }

    fun readLong(): Long {
        val longBytes = LongArray(8)
        for (i in longBytes.indices) {
            longBytes[i] = bytes[pos++].toLong()
        }
        return (longBytes[0] shl 56) +
                ((longBytes[1] and 255) shl 48) +
                ((longBytes[2] and 255) shl 40) +
                ((longBytes[3] and 255) shl 32) +
                ((longBytes[4] and 255) shl 24) +
                (longBytes[5] and 255 shl 16) +
                (longBytes[6] and 255 shl 8) +
                (longBytes[7] and 255 shl 0)
    }

    fun readUTF(): String {
        val a = read()
        val b = read()
        var bytes = a shl 8 or b
        val sb = StringBuilder()
        while (bytes > 0) {
            bytes -= readUTFChar(sb)
        }
        return sb.toString()
    }

    private fun readUTFChar(builder: StringBuilder): Int {
        val a: Int = read()
        return when {
            a < 0x80 -> {
                builder.append(a.toChar())
                1
            }
            a < 0xc0 -> {
                builder.append('?')
                1
            }
            a < 0xe0 -> {
                val b = read()
                if (b and 0xc0 != 0x80) {
                    builder.append('?')
                    // probably a US-ASCII char after a Latin-1 char
                    builder.append(b.toChar())
                } else {
                    builder.append((a and 0x1F shl 6 or b and 0x3F).toChar())
                }
                2
            }
            a < 0xf0 -> {
                val b = read()
                if (b and 0xc0 != 0x80) {
                    builder.append('?')
                    builder.append(b.toChar())
                    return 2
                }
                val c = read()
                if (c and 0xc0 != 0x80) {
                    // probably a US-ASCII char after two Latin-1 chars?
                    builder.append('?').append('?')
                    builder.append(c.toChar())
                } else {
                    builder.append((a and 0x0F shl 12 or (b and 0x3F shl 6) or (c and 0x3F)).toChar())
                }
                3
            }
            else -> {
                builder.append('?')
                1
            }
        }
    }

    private fun read(): Int = if (pos >= bytes.size) -1 else bytes[pos++].toInt() and 0xFF
}