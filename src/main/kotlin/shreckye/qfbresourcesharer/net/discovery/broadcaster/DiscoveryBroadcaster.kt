package shreckye.qfbresourcesharer.net.discovery.broadcaster

import shreckye.qfbresourcesharer.net.discovery.DISCOVERY_SERVICE_DEFAULT_PORT
import java.lang.Thread.sleep
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import kotlin.concurrent.thread

/**
 * A broadcaster that broadcasts a UDP message to notify the availability of this peer.
 */
class DiscoveryBroadcaster(
    val port: Int = DISCOVERY_SERVICE_DEFAULT_PORT,
    val intervalInMillis: Long = DEFAULT_INTERVAL_IN_MILLIS
) {
    companion object {
        const val BROADCAST_IP: String = "255.255.255.255"
        const val DEFAULT_INTERVAL_IN_MILLIS: Long = 1000
        val emptyBuffer: ByteArray = ByteArray(0)
    }

    val broadcastAddress = InetSocketAddress(BROADCAST_IP, port)


    val datagramSocket:DatagramSocket = DatagramSocket()

    init {
        datagramSocket.broadcast = true
    }

    val thread: Thread = thread {
        while (!datagramSocket.isClosed) {
            datagramSocket.send(DatagramPacket(emptyBuffer, 0, broadcastAddress))
            sleep(intervalInMillis)
        }
    }

    fun close() {
        datagramSocket.close()
    }
}
