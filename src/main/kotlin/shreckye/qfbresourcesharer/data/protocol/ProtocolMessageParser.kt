package shreckye.qfbresourcesharer.data.protocol

import java.io.InputStream

interface ProtocolMessageParser<Action : Any, Data : Any?, PM : ProtocolMessage<Action, Data>> {
    fun parseMessage(inputStream: InputStream): PM =
        createMessage(parseAction(inputStream), parseData(inputStream))

    fun createMessage(action: Action, data: Data): PM
    fun parseAction(inputStream: InputStream): Action
    fun parseData(inputStream: InputStream): Data
}