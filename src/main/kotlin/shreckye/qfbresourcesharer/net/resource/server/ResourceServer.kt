package shreckye.qfbresourcesharer.net.resource.server

import shreckye.qfbresourcesharer.data.protocol.resource.DOWNLOAD
import shreckye.qfbresourcesharer.data.protocol.resource.PING_PONG
import shreckye.qfbresourcesharer.data.protocol.resource.QUERY
import shreckye.qfbresourcesharer.data.protocol.resource.request.DownloadRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.PingRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.QueryRequest
import shreckye.qfbresourcesharer.data.protocol.resource.response.ResourceResponse
import shreckye.qfbresourcesharer.net.resource.DEFAULT_RESOURCE_SERVICE_PORT
import shreckye.qfbresourcesharer.util.concurrent.executeAndPrintException
import java.lang.Thread.interrupted
import java.net.ServerSocket
import java.net.SocketException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class ResourceServer(
    resourceService: ResourceService,
    val port: Int = DEFAULT_RESOURCE_SERVICE_PORT
) {
    val serverSocket: ServerSocket = ServerSocket(port)
    val connections: MutableList<ResourceServerMessenger> = Vector()
    val executorService: ExecutorService = Executors.newCachedThreadPool()
    val acceptThread: Thread = thread {
        while (!interrupted()) {
            try {
                val socket = serverSocket.accept()
                val messenger = ResourceServerMessenger(socket)
                connections.add(messenger)

                executorService.executeAndPrintException {
                    while (!interrupted()) {
                        val request = messenger.receiveMessage()
                        when (request.action) {
                            PING_PONG -> resourceService.servePing(messenger, request as PingRequest)
                            QUERY -> resourceService.serveQuery(messenger, request as QueryRequest)
                            DOWNLOAD -> resourceService.serveDownload(messenger, request as DownloadRequest)
                            else -> throw IllegalArgumentException("invalid request type")
                        }
                    }
                }
            } catch (e: SocketException) {
                if (!interrupted())
                    e.printStackTrace()
                else
                    return@thread
            }
        }
    }

    fun sendToAll(response: ResourceResponse<*>) {
        for (connection in connections)
            executorService.executeAndPrintException {
                connection.sendMessage(response)
            }
    }

    fun close() {
        acceptThread.interrupt()
        executorService.shutdownNow()
        for (connection in connections)
            connection.close()
        serverSocket.close()
    }
}