package shreckye.qfbresourcesharer.data

import java.net.InetAddress

data class QueryResult(var peerCount: Byte, val filename: String, val fileSize: Long, val address: InetAddress)