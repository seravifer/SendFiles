package sendFiles.model

import tornadofx.*
import java.io.File

class FileModel : ItemViewModel<FileTransferInfo>() {
    val file = bind { item?.valueProperty() }
    val progress = bind { item?.progressProperty() }
}