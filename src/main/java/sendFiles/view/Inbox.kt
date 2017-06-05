package sendFiles.view

import javafx.scene.control.ListView
import javafx.scene.layout.AnchorPane
import sendFiles.component.InboxFileListFragment
import sendFiles.controller.MainController
import sendFiles.model.ProgressiveModel
import tornadofx.*
import java.io.File

/**
 * Created by David on 05/06/2017.
 */
class Inbox : View() {
    val controller by inject<MainController>()

    override val root by fxml<AnchorPane>()

    val inboxID by fxid<ListView<ProgressiveModel<File>>>()

    init {
        inboxID.cellFragment(InboxFileListFragment::class)
        inboxID.items = controller.downloaded
    }
}
