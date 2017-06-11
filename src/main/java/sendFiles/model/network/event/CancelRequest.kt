package sendFiles.model.network.event

import sendFiles.model.FileInfo
import sendFiles.model.FileTransferInfo
import sendFiles.model.network.FileConnection
import tornadofx.FXEvent

/**
 * Created by David Olmos on 10/06/2017.
 */
class CancelRequest(val fileTransferInfo: FileTransferInfo<out FileConnection<FileInfo>>) : FXEvent() {
}