package shreckye.qfbresourcesharer.codec

import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress

fun Int.toBytes(): ByteArray =
    byteArrayOf((this shr 24).toByte(), (this shr 16).toByte(), (this shr 8).toByte(), toByte())

fun ByteArray.toInt(): Int =
    this[0].toInt() shl 24 or this[1].toInt() shl 16 or this[2].toInt() shl 8 or this[3].toInt()

fun Long.toBytes(): ByteArray =
    byteArrayOf(
        (this shr 56).toByte(),
        (this shr 48).toByte(),
        (this shr 40).toByte(),
        (this shr 32).toByte(),
        (this shr 24).toByte(),
        (this shr 16).toByte(),
        (this shr 8).toByte(),
        toByte()
    )

fun ByteArray.toLong(): Long =
    this[0].toLong() shl 56 or
            this[1].toLong() shl 48 or
            this[2].toLong() shl 40 or
            this[3].toLong() shl 32 or
            this[4].toLong() shl 24 or
            this[5].toLong() shl 16 or
            this[6].toLong() shl 8 or
            this[7].toLong()

fun readLong(inputStream: InputStream): Long {
    val buffer = ByteArray(Long.SIZE_BYTES)
    inputStream.read(buffer)
    return buffer.toLong()
}

fun write1ByteLengthString(outputStream: OutputStream, string: String) {
    val length = string.length
    if (length >= 256)
        throw IllegalArgumentException("filename size can't exceed 256")
    outputStream.write(length)
    outputStream.write(string.toByteArray())
}

fun read1ByteLengthString(inputStream: InputStream): String {
    val length = inputStream.read()
    val buffer = ByteArray(length)
    inputStream.read(buffer)
    return String(buffer)
}

fun readIpv4Address(`in`: InputStream): InetAddress {
    val buffer = ByteArray(4)
    `in`.read(buffer)
    return InetAddress.getByAddress(buffer)
}