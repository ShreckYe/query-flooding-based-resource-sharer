package shreckye.qfbresourcesharer.net.resource

import shreckye.qfbresourcesharer.data.FileTransfer
import shreckye.qfbresourcesharer.data.Pong
import shreckye.qfbresourcesharer.data.QueryResult

interface ResourcePeerCallback {
    fun onPong(pong:Pong)
    fun onQueryHit(queryResult: QueryResult)
    fun onDownload(fileTransfer: FileTransfer)
}