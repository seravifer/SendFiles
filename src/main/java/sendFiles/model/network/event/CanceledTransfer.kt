package sendFiles.model.network.event

import sendFiles.model.FileTransferInfo
import tornadofx.*

/**
 * Created by David on 09/06/2017.
 */
class CanceledTransfer<T>(val fileTransferInfo: FileTransferInfo<T>) : FXEvent() {
    init {
        fileTransferInfo.state = FileTransferInfo.FileState.CANCELED
    }
}