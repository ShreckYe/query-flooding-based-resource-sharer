package shreckye.qfbresourcesharer.data.protocol.resource

import shreckye.qfbresourcesharer.data.protocol.ProtocolMessage
import java.io.OutputStream

abstract class ResourceMessage<Data : Any?>(
    override val action: Byte,
    override val data: Data
) : ProtocolMessage<Byte, Data> {
    override fun writeAction(outputStream: OutputStream) {
        outputStream.write(action.toInt())
    }
}