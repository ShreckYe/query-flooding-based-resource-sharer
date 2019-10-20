package shreckye.qfbresourcesharer.data.protocol.resource.response

import shreckye.qfbresourcesharer.codec.*
import shreckye.qfbresourcesharer.data.QueryResult
import shreckye.qfbresourcesharer.data.protocol.resource.QUERY
import java.io.InputStream
import java.io.OutputStream

class QueryResponse(data: QueryResult) : ResourceResponse<QueryResult>(QUERY, data) {
    override fun writeData(outputStream: OutputStream) {
        outputStream.write(data.peerCount.toInt())
        write1ByteLengthString(outputStream, data.filename)
        outputStream.write(data.fileSize.toBytes())
        outputStream.write(data.address.address)
    }

    companion object : Parser<QueryResult, QueryResponse> {
        override fun createMessageWithDataOnly(data: QueryResult): QueryResponse = QueryResponse(data)
        override fun parseData(inputStream: InputStream): QueryResult =
            QueryResult(inputStream.read().toByte(), read1ByteLengthString(inputStream), readLong(inputStream), readIpv4Address(inputStream))
    }
}