package shreckye.qfbresourcesharer.net.resource.client

import shreckye.qfbresourcesharer.data.FileTransfer
import shreckye.qfbresourcesharer.data.Ping
import shreckye.qfbresourcesharer.data.Query
import shreckye.qfbresourcesharer.data.protocol.resource.DOWNLOAD
import shreckye.qfbresourcesharer.data.protocol.resource.PING_PONG
import shreckye.qfbresourcesharer.data.protocol.resource.QUERY
import shreckye.qfbresourcesharer.data.protocol.resource.request.DownloadRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.PingRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.QueryRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.ResourceRequest
import shreckye.qfbresourcesharer.data.protocol.resource.response.DownloadResponse
import shreckye.qfbresourcesharer.data.protocol.resource.response.PongResponse
import shreckye.qfbresourcesharer.data.protocol.resource.response.QueryResponse
import shreckye.qfbresourcesharer.net.computeNewAvgDelay
import shreckye.qfbresourcesharer.net.resource.DEFAULT_INITIAL_PEER_COUNT
import shreckye.qfbresourcesharer.net.resource.DEFAULT_RESOURCE_SERVICE_PORT
import shreckye.qfbresourcesharer.util.concurrent.executeAndPrintException
import java.lang.Thread.interrupted
import java.lang.Thread.sleep
import java.net.ConnectException
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.math.min

class ResourceClient(
    val peers: ConcurrentMap<InetAddress, Long>,
    val resourceClientCallback: ResourceClientCallback,
    val initialPeerCount: Byte = DEFAULT_INITIAL_PEER_COUNT,
    var maxEdges: Int = DEFAULT_MAX_EDGES,
    val port: Int = DEFAULT_RESOURCE_SERVICE_PORT,
    val refreshIntervalInMillis: Long = DEFAULT_REFRESH_INTERVAL_IN_MILLIS
) {
    companion object {
        const val DEFAULT_MAX_EDGES: Int = 10
        val defaultThreads: Int = Runtime.getRuntime().availableProcessors() * 2
        const val DEFAULT_REFRESH_INTERVAL_IN_MILLIS: Long = 1000
    }

    val executorService: ExecutorService = Executors.newCachedThreadPool()
    val connections: ConcurrentMap<InetAddress, ResourceClientMessenger> = ConcurrentHashMap()

    val refreshThread: Thread = thread {
        while (!interrupted()) {
            val sortedAddresses = peers.entries
                .sortedWith(compareBy { it.value })
            val edgeAddresses = sortedAddresses
                .subList(0, min(maxEdges, sortedAddresses.size))
                .map { it.key }

            for (address in edgeAddresses) {
                var oldConnection = connections[address]
                if (oldConnection != null && !oldConnection.socket.isConnected) {
                    oldConnection.close()
                    connections.remove(address)
                    oldConnection = null
                }

                if (oldConnection == null)
                    executorService.executeAndPrintException {
                        val oldMessenger = connections[address]
                        if (oldMessenger != null && oldMessenger.socket.isConnected)
                            return@executeAndPrintException

                        var time = System.currentTimeMillis()
                        val socket = try {
                            Socket(address, port)
                        } catch (e: ConnectException) {
                            //println("Failed to connect to: ${address.hostAddress}")
                            return@executeAndPrintException
                        }
                        time = System.currentTimeMillis() - time
                        peers.merge(address, time) { avgDelay, newDelay -> computeNewAvgDelay(avgDelay, newDelay) }

                        val messenger = ResourceClientMessenger(socket)
                        connections[address] = messenger

                        while (!interrupted()) {
                            val response = messenger.receiveMessage()
                            when (response.action) {
                                PING_PONG -> resourceClientCallback.onPong(messenger, response as PongResponse)
                                QUERY -> resourceClientCallback.onQuery(messenger, response as QueryResponse)
                                DOWNLOAD -> resourceClientCallback.onDownload(
                                    messenger,
                                    response as DownloadResponse
                                )
                                else -> throw IllegalArgumentException("invalid request type")
                            }
                        }
                        messenger.close()
                    }

                for (address in connections.keys - edgeAddresses)
                    executorService.executeAndPrintException {
                        connections.remove(address)!!.close()
                    }

                sleep(refreshIntervalInMillis)
            }
        }
    }

    fun sendToAll(request: ResourceRequest<*>) {
        for (connection in connections)
            executorService.executeAndPrintException {
                connection.value.sendMessage(request)
            }
    }

    fun ping() = sendToAll(PingRequest(Ping(initialPeerCount)))
    fun query(filename: String) = sendToAll(QueryRequest(Query(initialPeerCount, 0, filename)))
    fun download(host: String, filename: String): FileTransfer {
        val messenger = ResourceClientMessenger(Socket(host, port))
        messenger.sendMessage(DownloadRequest(filename))
        return (messenger.receiveMessage() as DownloadResponse).data
    }

    fun close() {
        refreshThread.interrupt()
        for (connection in connections)
            connection.value.close()
        executorService.shutdownNow()
    }
}