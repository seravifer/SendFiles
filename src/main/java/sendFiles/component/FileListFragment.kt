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
    private val progressID: ProgressBar by fxid()
    private val percentID: Label by fxid()
    private val infoID: Label by fxid()
    val closeButtonID: Button by fxid()

    init {
        nameID.textProperty().bind(model.file.stringBinding { it?.name })

        progressID.progressProperty().bind(model.progress)
        progressID.visibleProperty().bind(progressID.progressProperty().lessThan(1.0))

        percentID.bind(Bindings.format("%.0f%s", model.progress.doubleBinding { (it?.toDouble() ?: 0.0) * 100 }, "%"))
        percentID.visibleProperty().bind(percentID.textProperty().isNotEqualTo("100%"))

        when(this) {
            is HomeFileListFragment -> closeButtonID.setOnAction { homeComponent.files.remove(model.item) }
            else -> {
                infoID.text = SimpleDateFormat("HH:mm").format(Date())
                closeButtonID.isVisible = false
            }
        }
    }
}

class HomeFileListFragment : FileListFragment()
class InboxFileListFragment : FileListFragment()
class OutboxFileListFragment : FileListFragment()