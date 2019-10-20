package shreckye.qfbresourcesharer.net

import shreckye.qfbresourcesharer.data.FileTransfer
import shreckye.qfbresourcesharer.net.discovery.broadcaster.DiscoveryBroadcaster
import shreckye.qfbresourcesharer.net.discovery.receiver.DiscoveryReceiver
import shreckye.qfbresourcesharer.net.resource.ResourcePeer
import shreckye.qfbresourcesharer.net.resource.ResourcePeerCallback
import java.io.File
import java.net.InetAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class QfbResourceSharer(
    val directory: String,
    resourcePeerCallback: ResourcePeerCallback,
    initialPeerHosts: List<String> = listOf("120.79.27.133")
) {
    // A tracker keeping the hosts available and the corresponding delay
    private val peers: ConcurrentMap<InetAddress, Long> = ConcurrentHashMap()

    init {
        for (host in initialPeerHosts)
            peers[InetAddress.getByName(host)] = 0
    }

    val discoveryBroadcaster: DiscoveryBroadcaster = DiscoveryBroadcaster()
    val discoveryReceiver: DiscoveryReceiver = DiscoveryReceiver({ peers.putIfAbsent(it, 0) })
    val resourcePeer: ResourcePeer = ResourcePeer(peers, File(directory), resourcePeerCallback)

    fun close() {
        discoveryBroadcaster.close()
        discoveryReceiver.close()
        resourcePeer.close()
    }

    fun ping() = resourcePeer.resourceClient.ping()
    fun query(filename: String) = resourcePeer.resourceClient.query(filename)
    fun download(host: String, filename: String): FileTransfer = resourcePeer.resourceClient.download(host, filename)
}