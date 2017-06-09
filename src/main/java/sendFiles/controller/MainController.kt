package sendFiles.controller

import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.WindowEvent
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import sendFiles.model.FileTransferInfo
import sendFiles.model.network.FileConnection
import sendFiles.model.network.FileReceiveConnection
import sendFiles.model.network.FileSendConnection
import sendFiles.model.network.Server
import sendFiles.model.network.event.ReceptionRequest
import sendFiles.model.network.event.SendRequest
import sendFiles.util.*
import tornadofx.*
import java.io.File

class MainController : Controller() {
    val downloadsDir = app.getDownloadPath().toPath()

    val sent = observableListOf<FileTransferInfo<FileSendConnection>>()
    val downloaded = observableListOf<FileTransferInfo<FileReceiveConnection>>()

    val actualPort by lazy { Server.localPort }

    init {
        val previousHandler = primaryStage.onCloseRequest
        primaryStage.onCloseRequest = EventHandler<WindowEvent> {
            if (confirmExit()) {
                previousHandler?.handle(it)
            } else {
                it.consume()
            }
        }

        subscribe<SendRequest> {
            launch(JavaFx) {
                sent.addAll(it.filesTransferProgress)
                it.sendHandler.sendHeaders(it.filesTransferProgress)
            }
        }

        subscribe<ReceptionRequest> {
            launch(JavaFx) {
                it.receptionHandler.receiveHeaders(downloadsDir).consumeEach {
                    downloaded.add(it)
                }
            }
        }
    }

    fun confirmExit(): Boolean {
        val transferring: (FileTransferInfo<out FileConnection>) -> Boolean = { it.state == FileTransferInfo.FileState.TRANSFERRING }
        val sending = sent.any(transferring)
        val downloading = downloaded.any(transferring)

        val message = when {
            sending && downloading -> "You have uploads and downloads in process"
            sending -> "You have uploads in process"
            downloading -> "You have downloads in process"
            else -> ""
        }

        return if (message.isNotBlank()) {
            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.headerText = message
            alert.contentText = "Are you sure you want to exit?"

            val buttonTypeWait = ButtonType("Wait")
            val buttonTypeExit = ButtonType("Exit")

            alert.buttonTypes.setAll(buttonTypeWait, buttonTypeExit)

            val result = alert.showAndWait()
            result.get() != buttonTypeWait
        } else true
    }

    fun close() {
        Server.close()
    }
}