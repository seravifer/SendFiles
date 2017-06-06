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
 * Created by David on 06/06/2017.
 */
object NetworkHandler {

    fun download(socket: Socket, dirPath: Path, modelList: ObservableList<ProgressiveModel<File>>): Job = launch(JavaFx) {
        val model = ProgressiveModel<File>()
        modelList.add(model)
        if (!socket.isClosed && !socket.isInputShutdown) {
            val inputStream = socket.getInputStream()
            val infoReader = DataInputStream(inputStream)

            model.stateProperty().addListener { _, _, new -> if (new == ProgressiveModel.FileState.CANCELED) inputStream.close() }
            model.state = ProgressiveModel.FileState.RECEIVING

            val name = run(CommonPool) { infoReader.readUTF() }
            val size = run(CommonPool) { infoReader.readLong() }
            model.value = File(dirPath.toFile(), name)

            model.state = ProgressiveModel.FileState.WAITING
            model.stateProperty().addAsyncListener { _, old, new ->
                if (old == ProgressiveModel.FileState.WAITING && new == ProgressiveModel.FileState.ACCEPTED) {
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
                    if (model.progress.toDouble() < 1.0) model.state = ProgressiveModel.FileState.CANCELED
                }
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
}
