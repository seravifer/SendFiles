package sendFiles.model.network

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import tornadofx.Injectable
import java.io.*
import java.net.InetAddress

/**
 * Created by David Olmos on 10/06/2017.
 */
interface Connection : Injectable, Closeable {
    val inputStream: InputStream
    val outputStream: OutputStream

    val infoReader: DataInputStream
    val infoSender: DataOutputStream

    val targetPort: Int
    val ownPort: Int
    val targetIP: InetAddress

    suspend fun sendAck(ack: Boolean) {
        run(CommonPool) { infoSender.writeBoolean(ack) }
    }
    suspend fun receiveAck(): Boolean = run(CommonPool) { infoReader.readBoolean() }
}

