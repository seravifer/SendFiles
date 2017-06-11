package sendFiles.model

import sendFiles.model.network.FileConnection
import tornadofx.*
import java.io.File

class FileModel<T : FileConnection<FileTransferInfo<T>>> : ItemViewModel<FileTransferInfo<T>>() {
    val file = bind { item?.valueProperty() }
    val progress = bind { item?.progressProperty() }
}