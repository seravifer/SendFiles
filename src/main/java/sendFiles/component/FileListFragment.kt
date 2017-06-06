package sendFiles.component

import javafx.beans.binding.Bindings
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.AnchorPane
import sendFiles.model.FileModel
import sendFiles.model.ProgressiveModel
import sendFiles.view.Home
import tornadofx.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


sealed class FileListFragment : ListCellFragment<ProgressiveModel<File>>() {
    val model = FileModel().bindTo(this)
    val homeComponent by inject<Home>()

    override val root by fxml<AnchorPane>("FileListFragment.fxml")

    private val nameID: Label by fxid()
    val closeButton: Button by fxid("closeID")
    private val progressID: ProgressBar by fxid()
    private val percentID: Label by fxid()
    private val infoID: Label by fxid()

    init {
        nameID.textProperty().bind(model.file.stringBinding { it?.name })

        progressID.progressProperty().bind(model.progress)
        progressID.visibleProperty().bind(progressID.progressProperty().greaterThanOrEqualTo(1.0))

        percentID.bind(Bindings.format("%.0f%s", model.progress.doubleBinding { (it?.toDouble() ?: 0.0) * 100 }, "%"))

        when(this) {
            is HomeFileListFragment -> closeButton.setOnAction { homeComponent.files.remove(model.item) }
            is OutboxFileListFragment -> {
                val hourFormat = SimpleDateFormat("HH:mm")
                infoID.text = hourFormat.format(Date())

                closeButton.isVisible = false
                progressID.isVisible = false
                percentID.isVisible = false
            }
            else -> closeButton.isVisible = false
        }
    }
}

class HomeFileListFragment : FileListFragment()
class InboxFileListFragment : FileListFragment()
class OutboxFileListFragment : FileListFragment()