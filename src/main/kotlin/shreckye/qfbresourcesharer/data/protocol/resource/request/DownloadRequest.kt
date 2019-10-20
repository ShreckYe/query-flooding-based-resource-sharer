package shreckye.qfbresourcesharer.data.protocol.resource.request

import shreckye.qfbresourcesharer.codec.read1ByteLengthString
import shreckye.qfbresourcesharer.codec.write1ByteLengthString
import shreckye.qfbresourcesharer.data.protocol.resource.DOWNLOAD
import java.io.InputStream
import java.io.OutputStream

class DownloadRequest(data: String) : ResourceRequest<String>(DOWNLOAD, data) {
    override fun writeData(outputStream: OutputStream) = write1ByteLengthString(outputStream, data)

    companion object : Parser<String, DownloadRequest> {
        override fun createMessageWithDataOnly(data: String): DownloadRequest = DownloadRequest(data)

        override fun parseData(inputStream: InputStream): String = read1ByteLengthString(inputStream)
    }
}