package shreckye.qfbresourcesharer.data.protocol.resource.response

import shreckye.qfbresourcesharer.codec.read1ByteLengthString
import shreckye.qfbresourcesharer.codec.readLong
import shreckye.qfbresourcesharer.codec.toBytes
import shreckye.qfbresourcesharer.codec.write1ByteLengthString
import shreckye.qfbresourcesharer.data.FileTransfer
import shreckye.qfbresourcesharer.data.protocol.resource.DOWNLOAD
import java.io.InputStream
import java.io.OutputStream

class DownloadResponse(data: FileTransfer) : ResourceResponse<FileTransfer>(DOWNLOAD, data) {
    override fun writeData(outputStream: OutputStream) {
        write1ByteLengthString(outputStream, data.filename)
        outputStream.write(data.length.toBytes())
        data.fileInput.copyTo(outputStream)
    }

    companion object : Parser<FileTransfer, DownloadResponse> {
        override fun createMessageWithDataOnly(data: FileTransfer): DownloadResponse = DownloadResponse(data)
        override fun parseData(inputStream: InputStream): FileTransfer =
            FileTransfer(read1ByteLengthString(inputStream), readLong(inputStream), inputStream)
    }
}