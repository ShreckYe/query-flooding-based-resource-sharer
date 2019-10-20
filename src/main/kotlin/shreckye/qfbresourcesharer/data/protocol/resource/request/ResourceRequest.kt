package shreckye.qfbresourcesharer.data.protocol.resource.request

import shreckye.qfbresourcesharer.data.protocol.resource.*
import java.io.InputStream
import java.lang.IllegalArgumentException

abstract class ResourceRequest<Data>(action: Byte, data: Data) : ResourceMessage<Data>(action, data) {
    interface Parser<Data, RR : ResourceRequest<Data>> : ResourceMessageParser<Data, RR> {
        fun parseMessageWithDataOnly(inputStream: InputStream) = createMessageWithDataOnly(parseData(inputStream))
        override fun createMessage(action: Byte, data: Data): RR = createMessageWithDataOnly(data)
        fun createMessageWithDataOnly(data: Data): RR
    }

    companion object : ResourceMessageFactoryParser<ResourceRequest<*>> {
        override fun parseMessage(inputStream: InputStream): ResourceRequest<*> {
            val action = inputStream.read().toByte()
            return when (action) {
                PING_PONG -> PingRequest.parseMessageWithDataOnly(inputStream)
                QUERY -> QueryRequest.parseMessageWithDataOnly(inputStream)
                DOWNLOAD -> DownloadRequest.parseMessageWithDataOnly(inputStream)
                else -> throw IllegalArgumentException("invalid action received")
            }
        }
    }
}