package shreckye.qfbresourcesharer.data.protocol.resource.request

import shreckye.qfbresourcesharer.data.Ping
import shreckye.qfbresourcesharer.data.protocol.resource.PING_PONG
import java.io.InputStream
import java.io.OutputStream

class PingRequest(data: Ping) : ResourceRequest<Ping>(PING_PONG, data) {
    override fun writeData(outputStream: OutputStream) {
        outputStream.write(data.peerCount.toInt())
        outputStream.write(data.distance.toInt())
    }

    companion object : Parser<Ping, PingRequest> {
        override fun createMessageWithDataOnly(data: Ping): PingRequest = PingRequest(data)
        override fun parseData(inputStream: InputStream): Ping =
            Ping(inputStream.read().toByte(), inputStream.read().toByte())
    }
}