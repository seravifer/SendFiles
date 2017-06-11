package sendFiles.util

import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.run
import sendFiles.model.FileInfo
import sendFiles.model.network.Connection
import sendFiles.model.network.FileConnection
import sendFiles.model.network.FileTransferCanceler
import java.io.*
import java.net.*

fun getIpAddress(): String =
        try {
            val myIpAws = URL("http://checkip.amazonaws.com")
            myIpAws.openStream().use {
                BufferedReader(InputStreamReader(it)).readLine()
            }
        } catch (e: SocketException) {
           localIP.hostAddress
        }

fun available(port: Int): Boolean = try {
    ServerSocket(port).close()
    true
} catch (ignored: IOException) {
    false
}

inline suspend fun InputStream.copyTo(out: OutputStream, crossinline progressUpdate: (bytesRead: Long) -> Unit) {
    val buff = ByteArray(4.K)
    var count = 0
    var totalSize = 0L
    while (this.read(buff).apply { count = this } > -1) {
        out.write(buff, 0, buff.size)
        totalSize += count
        run(JavaFx) { progressUpdate(totalSize) }
    }
}


/**
 * Solve a problem that confuses ip from VMs
 * And problem on Linux with non-loopback IP
 */
val localIP: InetAddress by lazy {
    NetworkInterface.getNetworkInterfaces().asSequence()
            .flatMap { it.inetAddresses.asSequence() }
            .filterNot { it.isLoopbackAddress }
            .filterIsInstance(Inet4Address::class.java)
            .first()
}

fun Socket.toConnection() = object : Connection {
    override val inputStream: InputStream by lazy { this@toConnection.getInputStream() }
    override val outputStream: OutputStream by lazy { this@toConnection.getOutputStream() }
    override val infoReader: DataInputStream by lazy { DataInputStream(inputStream) }
    override val infoSender: DataOutputStream by lazy { DataOutputStream(outputStream) }

    override val targetPort: Int = this@toConnection.port
    override val ownPort: Int = this@toConnection.localPort

    override val targetIP: InetAddress = this@toConnection.inetAddress

    override fun close() { this@toConnection.close() }
}

fun FileConnection<*>.createFileTransferCanceler(file: FileInfo): FileTransferCanceler =
        FileTransferCanceler(Socket(targetIP, targetPort).toConnection(), file)