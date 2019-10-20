package shreckye.qfbresourcesharer.data.protocol

import java.io.InputStream

interface ProtocolMessageFactoryParser<PM : ProtocolMessage<*, *>> {
    fun parseMessage(inputStream: InputStream): PM
}