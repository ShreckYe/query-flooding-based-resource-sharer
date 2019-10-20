package shreckye.qfbresourcesharer.data

class Query(var peerCount: Byte, var distance: Byte = 0, val filename: String) {
    fun moveOne(): Boolean {
        peerCount--
        distance++
        return peerCount > 0
    }
}