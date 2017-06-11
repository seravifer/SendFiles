package sendFiles.model.network.event

import sendFiles.model.FileInfo
import tornadofx.*

/**
 * Created by David on 09/06/2017.
 */
class CanceledTransfer<T>(val fileTransferInfo: FileInfo) : FXEvent()