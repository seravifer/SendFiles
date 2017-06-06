package sendFiles.view

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.stage.DirectoryChooser
import sendFiles.util.getDownloadPath
import sendFiles.util.setDownloadPatch
import tornadofx.*

class Setting : View() {
    override val root by fxml<AnchorPane>()

    val pathID by fxid<TextField>()
    val changeID by fxid<Button>()

    init {
        pathID.text = app.getDownloadPath()
        changeID.onAction = EventHandler<ActionEvent> {
            val directoryChooser = DirectoryChooser()
            val selectedDirectory = directoryChooser.showDialog(primaryStage)

            if (selectedDirectory != null) {
                pathID.text = selectedDirectory.absolutePath
                app.setDownloadPatch(selectedDirectory.absolutePath)
            }
        }
    }
}
