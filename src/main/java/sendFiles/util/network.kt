package sendFiles.util

import javafx.collections.ObservableList
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import sendFiles.model.ProgressiveModel
import java.io.*
import java.net.*
import java.nio.file.Path

/**
 * Created by David on 04/06/2017.
 */
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

fun download(socket: Socket, dirPath: Path, modelList: ObservableList<ProgressiveModel<File>>): Job = launch(JavaFx) {
    val model = ProgressiveModel<File>()
    modelList.add(model)
    if (!socket.isClosed && !socket.isInputShutdown) {
        val inputStream = socket.getInputStream()
        model.stateProperty().addListener { _, _, new -> if (new == ProgressiveModel.FileState.CANCELED) inputStream.close() }
        model.state = ProgressiveModel.FileState.RECEIVING
        val infoReader = DataInputStream(inputStream)
        val name = run(CommonPool) { infoReader.readUTF() }
        val size = run(CommonPool) { infoReader.readLong() }
        model.value = File(dirPath.toFile(), name)
        val fileStream = FileOutputStream(model.value)
        try {
            fileStream.use {
                run(CommonPool) {
                    inputStream.copyTo(fileStream) {
                        model.progress = (it.toDouble() / size).takeIf { it <= 1.0 } ?: 1.0
                    }
                }
            }
            model.state = ProgressiveModel.FileState.RECEIVED
        } catch(e: IOException) {
            model.state = ProgressiveModel.FileState.CANCELED
        }
    } else {
        model.state = ProgressiveModel.FileState.FAILED
    }
}

suspend fun uploadFile(socket: Socket, fileProgressive: ProgressiveModel<File>) {
    if (fileProgressive.value.exists() && !socket.isClosed && !socket.isOutputShutdown) {
        val outputStream = socket.getOutputStream()
        fileProgressive.stateProperty().addListener { _, _, new -> if (new == ProgressiveModel.FileState.CANCELED) outputStream.close() }
        fileProgressive.state = ProgressiveModel.FileState.SENDING
        val infoSender = DataOutputStream(outputStream)
        run(CommonPool) { infoSender.writeUTF(fileProgressive.value.name) }
        run(CommonPool) { infoSender.writeLong(fileProgressive.value.length()) }
        val fileStream = FileInputStream(fileProgressive.value)
        try {
            fileStream.use {
                run(CommonPool) {
                    fileStream.copyTo(outputStream) {
                        fileProgressive.progress = (it.toDouble() / fileProgressive.value.length()).takeIf { it <= 1.0 } ?: 1.0
                    }
                }
            }
            fileProgressive.state = ProgressiveModel.FileState.SENT
        } catch(e: IOException) {
            fileProgressive.state = ProgressiveModel.FileState.CANCELED
        }
    }
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
