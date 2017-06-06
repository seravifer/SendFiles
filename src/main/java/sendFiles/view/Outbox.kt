package sendFiles.view

import javafx.scene.control.ListView
import javafx.scene.layout.AnchorPane
import sendFiles.component.OutboxFileListFragment
import sendFiles.controller.MainController
import sendFiles.model.ProgressiveModel
import tornadofx.*
import java.io.File

class Outbox : View() {
    val controller by inject<MainController>()

    override val root by fxml<AnchorPane>()

    private val outboxID by fxid<ListView<ProgressiveModel<File>>>()

    init {
        outboxID.cellFragment(OutboxFileListFragment::class)
        outboxID.items = controller.sent
    }
}
