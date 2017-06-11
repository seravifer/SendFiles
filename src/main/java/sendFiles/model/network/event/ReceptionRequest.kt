package sendFiles.model.network.event

import sendFiles.model.network.FileReceiveConnection
import sendFiles.model.network.NetworkHandler
import sendFiles.util.getDownloadPath
import tornadofx.*
import java.net.Socket

/**
 * Created by David on 08/06/2017.
 */
class ReceptionRequest(val receptionHandler: FileReceiveConnection) : FXEvent() {
    val filesTransferInfo = receptionHandler.receiveHeaders()
}