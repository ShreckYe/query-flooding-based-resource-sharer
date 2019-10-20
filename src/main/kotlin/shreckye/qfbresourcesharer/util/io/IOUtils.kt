package shreckye.qfbresourcesharer.util.io

import java.io.InputStream
import java.io.OutputStream

fun InputStream.copyLimitedTo(out: OutputStream, size: Long, bufferSize: Int = DEFAULT_BUFFER_SIZE) {
    var sizeLeft = size
    val buffer = ByteArray(bufferSize)
    do {
        val bytes = if (sizeLeft > bufferSize) read(buffer) else read(buffer, 0, sizeLeft.toInt())
        sizeLeft -= bytes
        out.write(buffer, 0, bytes)
    } while (sizeLeft > 0)
}