package sendFiles.model.network

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import sendFiles.util.toConnection
import java.net.Socket

/**
 * Created by David Olmos on 10/06/2017.
 */
class ConnectionCauseReader(socket: Socket) : Connection by socket.toConnection() {
    suspend fun readProtocol() : String = run(CommonPool) { infoReader.readUTF() }
}