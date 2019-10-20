package shreckye.qfbresourcesharer.data.protocol.resource.response

import shreckye.qfbresourcesharer.codec.readIpv4Address
import shreckye.qfbresourcesharer.data.Pong
import shreckye.qfbresourcesharer.data.protocol.resource.PING_PONG
import java.io.InputStream
import java.io.OutputStream

class PongResponse(data: Pong) : ResourceResponse<Pong>(PING_PONG, data) {
    override fun writeData(outputStream: OutputStream) {
        outputStream.write(data.peerCount.toInt())
        if (data.address.address.size != 4)
            throw IllegalArgumentException("only IPv4 supported temporarily")
        outputStream.write(data.address.address)
    }

    companion object : Parser<Pong, PongResponse> {
        override fun createMessageWithDataOnly(data: Pong): PongResponse = PongResponse(data)
        override fun parseData(inputStream: InputStream): Pong =
            Pong(inputStream.read().toByte(), readIpv4Address(inputStream))

    }
}