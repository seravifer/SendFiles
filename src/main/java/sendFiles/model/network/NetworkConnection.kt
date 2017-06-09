package sendFiles.model.network

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.javafx.JavaFx
import sendFiles.model.FileTransferInfo
import sendFiles.util.copyTo
import sendFiles.util.getDownloadPath
import sendFiles.util.toPath
import tornadofx.*
import java.io.*
import java.net.Socket
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by David on 08/06/2017.
 */
sealed class NetworkConnection<T>(socket: Socket) : Component(), Closeable by socket {
    protected val inputStream: InputStream by lazy { socket.getInputStream() }
    protected val outputStream: OutputStream by lazy { socket.getOutputStream() }

    protected val infoReader: DataInputStream by lazy { DataInputStream(inputStream) }
    protected val infoSender: DataOutputStream by lazy { DataOutputStream(outputStream) }

    suspend fun sendAck(ack: Boolean) {
        run(CommonPool) { infoSender.writeBoolean(ack) }
    }

    suspend fun receiveAck(): Boolean = run(CommonPool) { infoReader.readBoolean() }
}

sealed class FileConnection(socket: Socket) : NetworkConnection<File>(socket) {
    protected suspend fun sendFileInfo(file: FileTransferInfo<*>) {
        run(CommonPool) {
            infoSender.writeUTF(file.value.name)
            infoSender.write(file.md5, 0, 16)
            infoSender.writeLong(file.size)
        }
    }

    abstract suspend fun receiveFileInfo(dirPath: Path = app.getDownloadPath().toPath()): FileTransferInfo<out FileConnection>
}

/**
 * Created by David on 08/06/2017.
 */
class FileSendConnection(socket: Socket) : FileConnection(socket) {

    private var filesInfo: List<FileTransferInfo<FileSendConnection>> by singleAssign()

    private val fileRequests by lazy {
        produce(CommonPool) {
            send(receiveFileInfo())
        }
    }

    private val listenJob = launch(CommonPool, CoroutineStart.LAZY) {
        fileRequests.consumeEach { request ->
            val localFileInfo = filesInfo.first { request == it }
            if (localFileInfo.state != FileTransferInfo.FileState.CANCELED) {
                sendAck(true)
                sendFileData(localFileInfo)
            }
            else sendAck(false)
        }
    }

    override suspend fun receiveFileInfo(dirPath: Path): FileTransferInfo<FileSendConnection> = FileTransferInfo(
            file = run(CommonPool) { dirPath.resolve(infoReader.readUTF()).toFile() },
            md5 = run(CommonPool) { infoReader.readBytes(16) },
            size = run(CommonPool) { infoReader.readLong() },
            handler = this
    )

    suspend fun sendFileData(fileInfo: FileTransferInfo<FileSendConnection>) {
        run(CommonPool) {
            fileInfo.value.inputStream().copyTo(outputStream) {
                fileInfo.progress = (it.toDouble() / fileInfo.size).takeIf { it <= 1.0 } ?: 1.0
            }
        }
    }

    fun sendHeaders(filesInfo: List<FileTransferInfo<FileSendConnection>>) {
        launch(CommonPool) {
            for (transfer in filesInfo) {
                sendAck(true)
                sendFileInfo(transfer)
            }
            sendAck(false)
            listenJob.start()
        }
        this.filesInfo = filesInfo
    }

}

/**
 * Created by David on 06/06/2017.
 */
class FileReceiveConnection(socket: Socket) : FileConnection(socket) {

    private val fileReceptionQueue = Channel<FileTransferInfo<FileReceiveConnection>>()

    init {
        launch(CommonPool) {
            for (file in fileReceptionQueue) {
                sendFileInfo(file)
                if (receiveAck()) receiveFileData(file)
                else fire()
            }
        }
    }

    override suspend fun receiveFileInfo(dirPath: Path): FileTransferInfo<FileReceiveConnection> = FileTransferInfo(
            file = run(CommonPool) { dirPath.resolve(infoReader.readUTF()).toFile() },
            md5 = run(CommonPool) { infoReader.readBytes(16) },
            size = run(CommonPool) { infoReader.readLong() },
            handler = this
    )

    suspend fun receiveFileData(fileInfo: FileTransferInfo<FileReceiveConnection>) {
        run(CommonPool) {
            inputStream.copyTo(fileInfo.value.outputStream()) {
                fileInfo.progress = (it.toDouble() / fileInfo.size).takeIf { it <= 1.0 } ?: 1.0
            }
        }
    }

    suspend fun requestFile(fileInfo: FileTransferInfo<FileReceiveConnection>) {
        fileReceptionQueue.send(fileInfo)
    }

    fun receiveHeaders(dirPath: Path) = produce(CommonPool) {
        while (receiveAck()) {
            send(receiveFileInfo(dirPath))
        }
    }

}