package sendFiles.util

import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.run
import java.io.*
import java.net.*

fun getIpAddress(): String =
        try {
            val myIpAws = URL("http://checkip.amazonaws.com")
            myIpAws.openStream().use {
                BufferedReader(InputStreamReader(it)).readLine()
            }
        } catch (e: SocketException) {
            InetAddress.getLocalHost().toString()
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
fun getLocalIP(): InetAddress? {
    val en = NetworkInterface.getNetworkInterfaces()
    while (en.hasMoreElements()) {
        val en2 = en.nextElement().inetAddresses
        while (en2.hasMoreElements()) {
            val addr = en2.nextElement()
            if (!addr.isLoopbackAddress && addr is Inet4Address) {
                return addr
            }
        }
    }
    return null
}