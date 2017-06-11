package sendFiles.controller

import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.WindowEvent
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import sendFiles.model.FileTransferInfo
import sendFiles.model.network.FileConnection
import sendFiles.model.network.FileReceiveConnection
import sendFiles.model.network.FileSendConnection
import sendFiles.model.network.Server
import sendFiles.model.network.event.ReceptionRequest
import sendFiles.model.network.event.SendRequest
import sendFiles.util.*
import tornadofx.*

class MainController : Controller() {
    val sent = observableListOf<FileTransferInfo<FileSendConnection>>()
    val downloaded = observableListOf<FileTransferInfo<FileReceiveConnection>>()

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
                sent.addAll(it.filesTransferInfo)
            }
        }

        subscribe<ReceptionRequest> {
            launch(JavaFx) {
                it.filesTransferInfo.consumeEach { fileInfo ->
                    downloaded.add(fileInfo)
                    //Dirty thing which shouldn't be there but I didn't know to do it at event init
                    setInScope(it.receptionHandler, fileInfo)
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