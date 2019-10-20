package shreckye.qfbresourcesharer.net.resource

import shreckye.qfbresourcesharer.data.FileTransfer
import shreckye.qfbresourcesharer.data.Pong
import shreckye.qfbresourcesharer.data.QueryResult
import shreckye.qfbresourcesharer.data.protocol.resource.request.DownloadRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.PingRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.QueryRequest
import shreckye.qfbresourcesharer.data.protocol.resource.response.DownloadResponse
import shreckye.qfbresourcesharer.data.protocol.resource.response.PongResponse
import shreckye.qfbresourcesharer.data.protocol.resource.response.QueryResponse
import shreckye.qfbresourcesharer.net.resource.client.ResourceClient
import shreckye.qfbresourcesharer.net.resource.client.ResourceClientCallback
import shreckye.qfbresourcesharer.net.resource.client.ResourceClientMessenger
import shreckye.qfbresourcesharer.net.resource.server.ResourceServer
import shreckye.qfbresourcesharer.net.resource.server.ResourceServerMessenger
import shreckye.qfbresourcesharer.net.resource.server.ResourceService
import java.io.File
import java.net.InetAddress
import java.util.concurrent.ConcurrentMap

class ResourcePeer(
    val peers: ConcurrentMap<InetAddress, Long>,
    val directory: File,
    val resourcePeerCallback: ResourcePeerCallback,
    val initialPeerCount: Byte = DEFAULT_INITIAL_PEER_COUNT
) : ResourceService, ResourceClientCallback {
    val resourceServer: ResourceServer = ResourceServer(this)
    val resourceClient: ResourceClient = ResourceClient(peers, this)

    fun close() {
        resourceServer.close()
        resourceClient.close()
    }

    override fun servePing(messenger: ResourceServerMessenger, pingRequest: PingRequest) {
        val ping = pingRequest.data
        ping.moveOne()
        val distance = ping.distance
        if (distance > 1 && distance <= initialPeerCount)
            messenger.sendMessage(PongResponse(Pong(distance, messenger.socket.localAddress)))

        if (ping.peerCount > 0)
            resourceClient.sendToAll(pingRequest)
    }

    override fun serveQuery(messenger: ResourceServerMessenger, queryRequest: QueryRequest) {
        val query = queryRequest.data
        query.moveOne()
        val distance = query.distance
        val filename = query.filename
        //println("Query received: $filename")
        if (distance <= DEFAULT_INITIAL_PEER_COUNT && filename in directory.list())
            messenger.sendMessage(
                QueryResponse(
                    QueryResult(
                        distance,
                        filename,
                        File(directory, filename).length(),
                        messenger.socket.inetAddress
                    )
                )
            )

        if (query.peerCount > 0)
            resourceClient.sendToAll(queryRequest)
    }

    override fun serveDownload(messenger: ResourceServerMessenger, downloadRequest: DownloadRequest) {
        val filename = downloadRequest.data
        if (filename in directory.list()) {
            val file = File(directory, filename)
            val fileIn = file.inputStream()
            messenger.sendMessage(DownloadResponse(FileTransfer(filename, file.length(), fileIn)))
            fileIn.close()
        }
    }

    override fun onPong(messenger: ResourceClientMessenger, pongResponse: PongResponse) {
        val pong = pongResponse.data
        pong.peerCount--

        if (peers.putIfAbsent(pong.address, 0) == null)
            resourcePeerCallback.onPong(pong)

        if (pong.peerCount > 0)
            resourceServer.sendToAll(pongResponse)
    }

    override fun onQuery(messenger: ResourceClientMessenger, queryResponse: QueryResponse) {
        val queryResult = queryResponse.data
        queryResult.peerCount--

        if (queryResult.peerCount > 0)
            resourceServer.sendToAll(queryResponse)
        else
            resourcePeerCallback.onQueryHit(queryResult)
    }

    override fun onDownload(messenger: ResourceClientMessenger, downloadResponse: DownloadResponse) {
        resourcePeerCallback.onDownload(downloadResponse.data)
    }
}