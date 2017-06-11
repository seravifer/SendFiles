package sendFiles.model.network.event

import sendFiles.model.FileTransferInfo
import sendFiles.model.network.FileSendConnection
import sendFiles.model.network.toConnection
import tornadofx.*
import java.io.File
import java.net.Socket

/**
 * Created by David on 08/06/2017.
 */
class SendRequest(files: List<File>, host: String, port: Int) : FXEvent() {
    val filesTransferInfo: List<FileTransferInfo<FileSendConnection>> =
            files.map { FileTransferInfo(it, handler = sendHandler) }
    val sendHandler = FileSendConnection(Socket(host, port).toConnection())

    init {
        filesTransferInfo.forEach { setInScope(sendHandler, it) }
        sendHandler.sendHeaders(filesTransferInfo)
    }
}