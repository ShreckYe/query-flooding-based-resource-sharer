package shreckye.qfbresourcesharer.data.protocol

import java.io.OutputStream

interface ProtocolMessage<Action : Any, Data : Any?> {
    val action: Action
    val data: Data
    fun writeMessage(outputStream: OutputStream) {
        writeAction(outputStream)
        writeData(outputStream)
    }

    fun writeAction(outputStream: OutputStream)
    fun writeData(outputStream: OutputStream)
}