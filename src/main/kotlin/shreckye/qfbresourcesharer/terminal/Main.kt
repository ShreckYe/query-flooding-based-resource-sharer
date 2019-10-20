package shreckye.qfbresourcesharer.terminal

import shreckye.qfbresourcesharer.data.FileTransfer
import shreckye.qfbresourcesharer.data.Pong
import shreckye.qfbresourcesharer.data.QueryResult
import shreckye.qfbresourcesharer.net.QfbResourceSharer
import shreckye.qfbresourcesharer.net.resource.ResourcePeerCallback
import shreckye.qfbresourcesharer.util.io.copyLimitedTo
import java.io.File
import java.net.SocketException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun main(args: Array<String>) {
    println("Query-flooding based resource sharer\nYongshun Ye\nDecember 16, 2018")
    val directory = if (args.isNotEmpty()) args[0] else ""
    val queryResults = Collections.newSetFromMap(ConcurrentHashMap<QueryResult, Boolean>())
    val qfbResourceSharer =
        QfbResourceSharer(directory, object : ResourcePeerCallback {
            override fun onPong(pong: Pong) {
                println("Pong received from: ${pong.address}")
            }

            override fun onQueryHit(queryResult: QueryResult) {
                println("Query hit: ${queryResult.filename}, ${queryResult.fileSize}, ")
                queryResults.add(queryResult)
            }

            override fun onDownload(fileTransfer: FileTransfer) {
                throw IllegalStateException("this method shouldn't be invoked here")
            }
        })

    val scanner = Scanner(System.`in`)
    var action: Int
    do {
        println("Enter an action: 1. send a ping; 2. query a resource; 3. download a resource directly; 4. download a resource from the queried list; 5. view peers; 6. view connected peers; 0. exit")
        action = scanner.nextInt()
        scanner.nextLine()
        when (action) {
            1 -> {
                qfbResourceSharer.ping()
                println("Ping sent. Pongs will be displayed when received.")
            }
            2 -> {
                println("Enter the filename you want to query:")
                val filename = scanner.nextLine()
                qfbResourceSharer.query(filename)
                println("Query sent. Results will be displayed when received.")
            }
            3 -> {
                try {
                    println("Enter the host you want to download from:")
                    val host = scanner.nextLine()
                    println("Enter the filename of the file you want to download:")
                    val filename = scanner.nextLine()
                    promptDownload(qfbResourceSharer, scanner, host, filename)
                } catch (e: SocketException) {
                    println("Download failed with an exception")
                    e.printStackTrace()
                }
            }
            4 -> {
                println("List of queried results:")
                val queryResultList = queryResults.toList()
                queryResultList.forEachIndexed { index, queryResult ->
                    println("$index. filename: ${queryResult.filename}, size: ${queryResult.fileSize}, address: ${queryResult.address.hostAddress}")
                }
                println("Enter the index of the result you want to download:")
                val index = scanner.nextInt()
                scanner.nextLine()
                val queryResult = queryResultList[index]
                promptDownload(qfbResourceSharer, scanner, queryResult.address.hostAddress, queryResult.filename)
            }
            5 -> {
                println("Peers:")
                for ((peerAddress, peerDelay) in qfbResourceSharer.resourcePeer.peers)
                    println("IP address: ${peerAddress.hostAddress}, average delay: $peerDelay")
            }
            6 -> {
                println("Connected server peers:")
                for (connectionAddress in qfbResourceSharer.resourcePeer.resourceClient.connections.keys)
                    println(connectionAddress.hostAddress)
                println("Connected client peers:")
                for (connection in qfbResourceSharer.resourcePeer.resourceServer.connections)
                    println(connection.socket.inetAddress.hostAddress)
            }
            0 -> println("Exiting.")
            else -> println("The action you entered is invalid.")
        }
    } while (action != 0)
    qfbResourceSharer.close()
}

fun promptDownload(qfbResourceSharer: QfbResourceSharer, scanner: Scanner, host: String, filename: String) {
    val fileTransfer = qfbResourceSharer.download(host, filename)
    println(fileTransfer.length)
    println("Connected. Enter the directory path where you want to save this file: ")
    val path = scanner.nextLine()
    println("Downloading.")
    File(path, filename).outputStream().use {
        fileTransfer.fileInput.copyLimitedTo(it, fileTransfer.length)
    }
    println("File downloaded.")
}