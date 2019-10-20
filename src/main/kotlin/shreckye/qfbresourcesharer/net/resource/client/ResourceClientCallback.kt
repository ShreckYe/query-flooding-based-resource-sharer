package shreckye.qfbresourcesharer.net.resource.client

import shreckye.qfbresourcesharer.data.protocol.resource.response.DownloadResponse
import shreckye.qfbresourcesharer.data.protocol.resource.response.PongResponse
import shreckye.qfbresourcesharer.data.protocol.resource.response.QueryResponse

interface ResourceClientCallback {
    fun onPong(messenger: ResourceClientMessenger, pongResponse: PongResponse)
    fun onQuery(messenger: ResourceClientMessenger, queryResponse: QueryResponse)
    fun onDownload(messenger: ResourceClientMessenger, downloadResponse: DownloadResponse)
}