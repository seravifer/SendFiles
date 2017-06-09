package sendFiles.model.network

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch
import sendFiles.model.network.event.ReceptionRequest
import sendFiles.util.available
import tornadofx.*
import java.io.Closeable
import java.net.ServerSocket
import java.net.Socket

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

    private val clients = produce<Socket>(CommonPool) {
        while (!server.isClosed) {
            val client = server.accept()
            log.info("Accepting connection of ${client.inetAddress}")
            send(client)
        }
    }

    val localPort by lazy { server.localPort }

    init {
        launch(CommonPool) {
            clients.consumeEach {
                fire(ReceptionRequest(it))
            }
        }
    }

    override fun close() {
        server.close()
        clients.cancel()
    }
}