package shreckye.qfbresourcesharer.net

fun computeNewAvgDelay(avgDelay: Long, newDelay: Long, alpha: Double = 0.8): Long =
    (avgDelay * alpha + newDelay * (1 - alpha)).toLong()