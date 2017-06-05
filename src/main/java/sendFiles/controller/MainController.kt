package sendFiles.controller

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch
import sendFiles.model.ProgressiveModel
import tornadofx.*
import sendFiles.util.*
import java.io.File
import java.net.ConnectException
import java.net.ServerSocket
import java.net.Socket

/**
 * Created by David on 04/06/2017.
 */
class MainController : Controller() {
    val downloadsDir = "C:\\Users\\David\\Desktop\\test".toPath()

    val sent = observableListOf<ProgressiveModel<File>>()
    val downloaded = observableListOf<ProgressiveModel<File>>()

    private val testPorts = 4444..4448

    private val server = try {
        testPorts.first { available(it) }
                .let {
                    log.info("Servidor corriendo en el puerto $it")
                    ServerSocket(it)
                }
    } catch (e: NoSuchElementException) {
        kotlin.error("Todos los puertos entre ${testPorts.first} y ${testPorts.endInclusive} están ocupados")
    }

    private val clients = produce<Socket>(CommonPool) {
        while (!server.isClosed) {
            val client = server.accept()
            log.info("Aceptando conexión de ${client.inetAddress}")
            send(client)
        }
    }

    init {
        launch(CommonPool) {
            clients.consumeEach { socket ->
                download(socket, downloadsDir, downloaded).invokeOnCompletion { socket.close() }
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
                        uploadFile(it, fileProgressive)
                    }
                } catch(e: ConnectException) {
                    fileProgressive.state = ProgressiveModel.FileState.FAILED
                }
            }
        }
    }

    fun close() {
        server.close()
        clients.cancel()
    }
}