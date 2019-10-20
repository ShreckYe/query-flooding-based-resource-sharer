package shreckye.qfbresourcesharer.util.concurrent

import java.util.concurrent.Executor

fun Executor.executeAndPrintException(command: () -> Unit) {
    this.execute {
        try {
            command()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}