package sendFiles.model.network.event

import sendFiles.model.FileTransferInfo
import sendFiles.model.network.FileReceiveConnection
import tornadofx.*

/**
 * Created by David on 09/06/2017.
 */
class AcceptDownload(val fileInfo: FileTransferInfo<FileReceiveConnection>) : FXEvent()