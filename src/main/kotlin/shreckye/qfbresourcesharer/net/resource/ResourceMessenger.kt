package shreckye.qfbresourcesharer.net.resource

import shreckye.qfbresourcesharer.data.protocol.resource.ResourceMessage
import shreckye.qfbresourcesharer.data.protocol.resource.ResourceMessageFactoryParser
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

abstract class ResourceMessenger<OutMessage : ResourceMessage<*>, InMessage : ResourceMessage<*>>(val socket: Socket) {
    val socketIn: InputStream = socket.getInputStream()
    val socketOut: OutputStream = socket.getOutputStream()
    fun sendMessage(outMessage: OutMessage) {
        synchronized(socketOut) {
            //println("Sending out message to ${socket.inetAddress.hostAddress}.")
            outMessage.writeMessage(socketOut)
            //println("Out message sent to ${socket.inetAddress.hostAddress}: $outMessage")
        }
    }

    fun receiveMessage(): InMessage {
        synchronized(socketIn) {
            //println("Receiving in message from ${socket.inetAddress.hostAddress}.")
            val inMessage = parser.parseMessage(socketIn)
            //println("In message received from ${socket.inetAddress.hostAddress}: $inMessage")
            return inMessage
        }
    }

    abstract val parser: ResourceMessageFactoryParser<InMessage>

    fun close() {
        socketIn.close()
        socketOut.close()

        socket.close()
    }

    override fun equals(other: Any?): Boolean {
        return other is ResourceMessenger<*, *> && socket == other.socket
    }

    override fun hashCode(): Int = socket.hashCode()
}