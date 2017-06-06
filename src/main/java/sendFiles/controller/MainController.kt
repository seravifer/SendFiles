package sendFiles.controller

import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.WindowEvent
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch
import sendFiles.model.NetworkHandler
import sendFiles.model.ProgressiveModel
import sendFiles.util.*
import tornadofx.*
import java.io.File
import java.net.ConnectException
import java.net.ServerSocket
import java.net.Socket

class MainController : Controller() {
    val downloadsDir = app.getDownloadPath().toPath()

    val sent = observableListOf<ProgressiveModel<File>>()
    val downloaded = observableListOf<ProgressiveModel<File>>()

    private val testPorts = 4444..4448
    var actualPort = "0000"

    private val server = try {
        testPorts.first { available(it) }
                .let {
                    log.info("Server running in the port $it")
                    actualPort = it.toString()
                    ServerSocket(it)
                }
    } catch (e: NoSuchElementException) {
        kotlin.error("All ports between ${testPorts.first} and ${testPorts.endInclusive} are busy")
    }

    private val clients = produce<Socket>(CommonPool) {
        while (!server.isClosed) {
            val client = server.accept()
            log.info("Accepting connection of ${client.inetAddress}")
            send(client)
        }
    }

    init {
        launch(CommonPool) {
            clients.consumeEach { socket ->
                NetworkHandler.download(socket, downloadsDir, downloaded).invokeOnCompletion { socket.close() }
            }
        }

        val previousHandler = primaryStage.onCloseRequest
        primaryStage.onCloseRequest = EventHandler<WindowEvent> {
            if (confirmExit()) {
                previousHandler?.handle(it)
            } else {
                it.consume()
            }
        }
    }

    fun send(list: List<File>, host: String, port: Int) {
        for (file in list) {
            val fileProgressive = ProgressiveModel(file)
            sent.add(fileProgressive)
            launch(CommonPool) {
                try {
                    Socket(host, port).use {
                        NetworkHandler.uploadFile(it, fileProgressive)
                    }
                } catch(e: ConnectException) {
                    fileProgressive.state = ProgressiveModel.FileState.FAILED
                }
            }
        }
    }

    fun confirmExit(): Boolean {
        val sending = sent.any { it.state == ProgressiveModel.FileState.SENDING }
        val downloading = downloaded.any { it.state == ProgressiveModel.FileState.RECEIVING }

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
        server.close()
        clients.cancel()
    }
}