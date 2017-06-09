package sendFiles.model.network.event

import sendFiles.model.network.FileReceiveConnection
import tornadofx.*
import java.net.Socket

/**
 * Created by David on 08/06/2017.
 */
class ReceptionRequest(socket: Socket) : FXEvent() {
    val receptionHandler = FileReceiveConnection(socket)
}