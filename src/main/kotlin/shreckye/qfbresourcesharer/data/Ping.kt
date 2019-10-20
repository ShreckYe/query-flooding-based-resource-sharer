package shreckye.qfbresourcesharer.data

class Ping(var peerCount: Byte, var distance: Byte = 0) {
    fun moveOne() {
        peerCount--
        distance++
    }
}