package shreckye.qfbresourcesharer.data.protocol.resource.request

import shreckye.qfbresourcesharer.codec.read1ByteLengthString
import shreckye.qfbresourcesharer.data.Query
import shreckye.qfbresourcesharer.data.protocol.resource.QUERY
import java.io.InputStream
import java.io.OutputStream

class QueryRequest(data: Query) : ResourceRequest<Query>(QUERY, data) {
    override fun writeData(outputStream: OutputStream) {
        outputStream.write(data.peerCount.toInt())
        outputStream.write(data.distance.toInt())
        if (data.filename.length >= 256)
            throw IllegalArgumentException("filename size can't exceed 256")
        outputStream.write(data.filename.length)
        outputStream.write(data.filename.toByteArray())
    }

    companion object : Parser<Query, QueryRequest> {
        override fun createMessageWithDataOnly(data: Query): QueryRequest = QueryRequest(data)
        override fun parseData(inputStream: InputStream): Query =
            Query(inputStream.read().toByte(), inputStream.read().toByte(), read1ByteLengthString(inputStream))
    }
}
