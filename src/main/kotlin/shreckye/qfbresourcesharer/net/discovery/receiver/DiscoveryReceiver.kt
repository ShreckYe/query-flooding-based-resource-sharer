package shreckye.qfbresourcesharer.net.discovery.receiver

import shreckye.qfbresourcesharer.net.discovery.DISCOVERY_SERVICE_DEFAULT_PORT
import java.lang.Thread.interrupted
import java.net.*
import kotlin.concurrent.thread

class DiscoveryReceiver(
    val discoveryCallback: (InetAddress) -> Unit,
    val port: Int = DISCOVERY_SERVICE_DEFAULT_PORT
) {
    companion object {
        const val WILDCARD_IP: String = "0.0.0.0"
    }

    val datagramSocket: DatagramSocket = DatagramSocket(InetSocketAddress(port))

    val buffer: ByteArray = ByteArray(1480)

    val receiverThread: Thread = thread {
        while (!interrupted()) {
            try {
                val datagramPacket = DatagramPacket(buffer, buffer.size)
                datagramSocket.receive(datagramPacket)
                discoveryCallback(datagramPacket.address)
            } catch (e: SocketException) {
                if (!interrupted())
                    e.printStackTrace()
                else
                    return@thread
            }
        }
    }

    fun close() {
        receiverThread.interrupt()
        datagramSocket.close()
    }
}