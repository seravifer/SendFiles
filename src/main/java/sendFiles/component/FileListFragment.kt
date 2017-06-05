package sendFiles.component

import javafx.beans.binding.Bindings
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.AnchorPane
import sendFiles.view.Home
import sendFiles.model.FileModel
import sendFiles.model.ProgressiveModel
import tornadofx.*
import java.io.File
import java.text.NumberFormat

sealed class FileListFragment : ListCellFragment<ProgressiveModel<File>>() {
    val model = FileModel().bindTo(this)
    val homeComponent by inject<Home>()

    override val root by fxml<AnchorPane>("FileListFragment.fxml")

    private val nameID: Label by fxid()
    val closeButton: Button by fxid("closeID")
    private val progressID: ProgressBar by fxid()
    private val percentID: Label by fxid()

    init {
        nameID.textProperty().bind(model.file.stringBinding { it?.name })

        progressID.progressProperty().bind(model.progress)
        progressID.visibleProperty().bind(progressID.progressProperty().greaterThanOrEqualTo(1.0))


        percentID.bind(Bindings.format("%.2f%s", model.progress.doubleBinding { (it?.toDouble() ?: 0.0) * 100 }, "%"))

        when(this) {
            is HomeFileListFragment -> closeButton.setOnAction { homeComponent.files.remove(model.item) }
            else -> closeButton.isVisible = false
        }
    }
}

class HomeFileListFragment : FileListFragment()
class InboxFileListFragment : FileListFragment()
class OutboxFileListFragment : FileListFragment()