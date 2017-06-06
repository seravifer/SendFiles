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
