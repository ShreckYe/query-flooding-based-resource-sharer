package shreckye.qfbresourcesharer.data.protocol.resource.response

import shreckye.qfbresourcesharer.data.protocol.resource.*
import java.io.InputStream

abstract class ResourceResponse<Data : Any?>(action: Byte, data: Data) : ResourceMessage<Data>(action, data) {
    interface Parser<Data, RR : ResourceResponse<Data>> : ResourceMessageParser<Data, RR> {
        fun parseMessageWithDataOnly(inputStream: InputStream) = createMessageWithDataOnly(parseData(inputStream))
        override fun createMessage(action: Byte, data: Data): RR = createMessageWithDataOnly(data)
        fun createMessageWithDataOnly(data: Data): RR
    }

    companion object : ResourceMessageFactoryParser<ResourceResponse<*>> {
        override fun parseMessage(inputStream: InputStream): ResourceResponse<*> {
            val action = inputStream.read().toByte()
            return when (action) {
                PING_PONG -> PongResponse.parseMessageWithDataOnly(inputStream)
                QUERY -> QueryResponse.parseMessageWithDataOnly(inputStream)
                DOWNLOAD -> DownloadResponse.parseMessageWithDataOnly(inputStream)
                else -> throw IllegalArgumentException("invalid action received")
            }
        }
    }
}