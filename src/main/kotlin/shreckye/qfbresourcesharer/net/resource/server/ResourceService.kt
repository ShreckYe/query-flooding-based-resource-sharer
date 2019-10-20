package shreckye.qfbresourcesharer.net.resource.server

import shreckye.qfbresourcesharer.data.protocol.resource.request.DownloadRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.PingRequest
import shreckye.qfbresourcesharer.data.protocol.resource.request.QueryRequest

interface ResourceService {
    fun servePing(messenger: ResourceServerMessenger, pingRequest: PingRequest)
    fun serveQuery(messenger: ResourceServerMessenger, queryRequest: QueryRequest)
    fun serveDownload(messenger: ResourceServerMessenger, downloadRequest: DownloadRequest)
}