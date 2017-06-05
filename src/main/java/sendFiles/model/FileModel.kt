package sendFiles.model

import tornadofx.*
import java.io.File

/**
 * Created by David on 05/06/2017.
 */
class FileModel : ItemViewModel<ProgressiveModel<File>>() {
    val file = bind { item?.valueProperty() }
    val progress = bind { item?.progressProperty() }
}