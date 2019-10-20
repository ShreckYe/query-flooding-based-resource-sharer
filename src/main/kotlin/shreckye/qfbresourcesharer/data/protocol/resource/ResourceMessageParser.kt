package shreckye.qfbresourcesharer.data.protocol.resource

import shreckye.qfbresourcesharer.data.protocol.ProtocolMessageParser
import java.io.InputStream

interface ResourceMessageParser<Data, RM : ResourceMessage<Data>> : ProtocolMessageParser<Byte, Data, RM> {
    override fun parseAction(inputStream: InputStream): Byte =
        inputStream.read().toByte()
}