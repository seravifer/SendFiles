package sendFiles.model

import javafx.collections.ObservableList
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import sendFiles.util.addAsyncListener
import sendFiles.util.copyTo
import java.io.*
import java.net.Socket
import java.nio.file.Path

/**
 * In order to cancel a transfer, one side of the connection must set the state of the model to CANCELED.
 * In order to accept the transfer, the destination must set its state model to ACCEPTED
 * @see ProgressiveModel.FileState
 * Created by David on 06/06/2017.
 */
object NetworkHandler {

    fun download(socket: Socket, dirPath: Path, modelList: ObservableList<ProgressiveModel<File>>): Job = launch(JavaFx) {
        val model = ProgressiveModel<File>()
        modelList.add(model)
        if (!socket.isClosed && !socket.isInputShutdown && !socket.isOutputShutdown) {
            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()
            val infoReader = DataInputStream(inputStream)
            val infoSender = DataOutputStream(outputStream)

            model.stateProperty().addListener { _, _, new -> if (new == ProgressiveModel.FileState.CANCELED) inputStream.close() }
            model.state = ProgressiveModel.FileState.RECEIVING

            val name = run(CommonPool) { infoReader.readUTF() }
            val size = run(CommonPool) { infoReader.readLong() }
            model.value = File(dirPath.toFile(), name)

            if (size <= 0) return@launch //Reject connections which don't use our protocol
            model.state = ProgressiveModel.FileState.WAITING
            model.stateProperty().addAsyncListener { _, old, new ->
                if (old == ProgressiveModel.FileState.WAITING) {
                    try {
                        when (new) {
                            ProgressiveModel.FileState.ACCEPTED -> {
                                run(CommonPool) { infoSender.writeBoolean(true) }
                                val fileStream = FileOutputStream(model.value)
                                fileStream.use {
                                    run(CommonPool) {
                                        inputStream.copyTo(fileStream) {
                                            model.progress = (it.toDouble() / size).takeIf { it <= 1.0 } ?: 1.0
                                        }
                                    }
                                }
                                model.state =
                                        if (model.progress.toDouble() < 1.0) ProgressiveModel.FileState.CANCELED
                                        else ProgressiveModel.FileState.RECEIVED
                            }
                            ProgressiveModel.FileState.CANCELED -> { run(CommonPool) { infoSender.writeBoolean(false) } }
                        }
                    } catch(e: IOException) {
                        model.state = ProgressiveModel.FileState.CANCELED
                    }
                }
            }
        } else {
            model.state = ProgressiveModel.FileState.FAILED
        }
    }

    suspend fun uploadFile(socket: Socket, fileProgressive: ProgressiveModel<File>) {
        if (fileProgressive.value.exists() && !socket.isClosed && !socket.isOutputShutdown && !socket.isInputShutdown) {
            val outputStream = socket.getOutputStream()
            val inputStream = socket.getInputStream()
            val infoSender = DataOutputStream(outputStream)
            val infoReader = DataInputStream(inputStream)

            fileProgressive.stateProperty().addListener { _, _, new -> if (new == ProgressiveModel.FileState.CANCELED) outputStream.close() }
            fileProgressive.state = ProgressiveModel.FileState.SENDING

            run(CommonPool) { infoSender.writeUTF(fileProgressive.value.name) }
            run(CommonPool) { infoSender.writeLong(fileProgressive.value.length()) }
            val ack = run(CommonPool) { infoReader.readBoolean() }
            val fileStream = FileInputStream(fileProgressive.value)
            if (ack) {
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
            } else {
                fileProgressive.state = ProgressiveModel.FileState.CANCELED
            }
        }
    }
}
