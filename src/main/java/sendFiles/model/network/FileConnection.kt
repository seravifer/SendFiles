package sendFiles.model.network

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import sendFiles.model.FileInfo
import sendFiles.model.FileTransferInfo
import sendFiles.model.network.event.AbortAllConnection
import sendFiles.util.copyTo
import sendFiles.util.getDownloadPath
import tornadofx.Component
import tornadofx.singleAssign
import java.nio.file.Path

interface FileConnection<out T : FileInfo> : Connection {
    suspend fun sendFileInfo(file: FileInfo) {
        run(CommonPool) {
            infoSender.writeUTF(file.name)
            infoSender.write(file.md5, 0, 16)
            infoSender.writeLong(file.size)
        }
    }

    suspend fun receiveFileInfo(dirPath: Path): T
}

/**
 * Created by David on 08/06/2017.
 */
class FileSendConnection(connection: Connection) : Component(), FileConnection<FileTransferInfo<FileSendConnection>>, Connection by connection {

    private var filesInfo: List<FileTransferInfo<FileSendConnection>> by singleAssign()

    private val fileRequests by lazy {
        produce(CommonPool) {
            send(receiveFileInfo(app.getDownloadPath()))
        }
    }

    private val listenJob = launch(CommonPool, CoroutineStart.LAZY) {
        fileRequests.consumeEach { request ->
            val localFileInfo = filesInfo.first { request == it }
            if (localFileInfo.state != FileTransferInfo.FileState.CANCELED) {
                sendAck(true)
                sendFileData(localFileInfo)
            } else sendAck(false)
        }
    }

    init {
        subscribe<AbortAllConnection> { close() }

        launch(CommonPool) {
            infoSender.writeUTF("FILE")
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

sealed class ClientConnection(connection: Connection) : Component(), Connection by connection {
    init {
        subscribe<AbortAllConnection> { close() }
    }
}

class FileTransferCanceler(connection: Connection, file: FileInfo) : ClientConnection(connection), FileConnection<FileInfo> {
    private val job: Job

    init {
        job = if (sender) launch(CommonPool) {
            infoSender.writeUTF("CANCEL")
            sendFileInfo(file)
        } else launch(CommonPool) {
            fire()
        }
    }

    override suspend fun receiveFileInfo(dirPath: Path): FileInfo = FileInfo.create(
            name = run(CommonPool) { infoReader.readUTF() },
            md5 = run(CommonPool) { infoReader.readBytes(16) },
            size = run(CommonPool) { infoReader.readLong() }
    )
}

/**
 * Created by David on 06/06/2017.
 */
class FileReceiveConnection(connection: Connection) : ClientConnection(connection), FileConnection<FileTransferInfo<FileReceiveConnection>> {

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

    fun receiveHeaders(dirPath: Path = app.getDownloadPath()) = produce(CommonPool) {
        while (receiveAck()) {
            send(receiveFileInfo(dirPath))
        }
        this.close()
    }

}