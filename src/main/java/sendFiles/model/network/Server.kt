package sendFiles.model.network

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch
import sendFiles.model.network.event.AbortAllConnection
import sendFiles.model.network.event.ReceptionRequest
import sendFiles.util.available
import tornadofx.*
import java.io.Closeable
import java.net.ServerSocket

/**
 * Created by David on 08/06/2017.
 */
object Server : Component(), Closeable {
    private val testPorts = 4444..4448

    private val server = try {
        testPorts.first { available(it) }
                .let {
                    log.info("Server running in the port $it")
                    ServerSocket(it)
                }
    } catch (e: NoSuchElementException) {
        kotlin.error("All ports between ${testPorts.first} and ${testPorts.endInclusive} are busy")
    }

    private val clients = produce<ClientConnection>(CommonPool) {
        while (!server.isClosed) {
            val clientSocket = server.accept()
            log.info("Accepting Connection of ${clientSocket.inetAddress}")
            val connectionReader = ConnectionCauseReader(clientSocket)
            send(
                    if (connectionReader.readProtocol() == "FILE") FileReceiveConnection(connectionReader)
                    else FileTransferCanceler(connectionReader)
            )
        }
    }

    private val fileClients = Channel<FileReceiveConnection>()
    private val metaClients = Channel<FileTransferCanceler>()

    val localPort by lazy { server.localPort }

    init {
        subscribe<AbortAllConnection> { close() }

        launch(CommonPool) {
            clients.consumeEach {
                when(it) {
                    is FileReceiveConnection -> fileClients.send(it)
                    is FileTransferCanceler -> metaClients.send(it)
                }
            }
        }
        launch(CommonPool) {
            fileClients.consumeEach {
                fire(ReceptionRequest(it))
            }
        }
        launch(CommonPool) {
            metaClients.consumeEach {

            }
        }
    }

    override fun close() {
        server.close()
        clients.cancel()
    }
}