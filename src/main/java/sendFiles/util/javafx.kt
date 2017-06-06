package sendFiles.util

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.WindowEvent
import sendFiles.model.ProgressiveModel
import java.io.File

fun <T> observableListOf(vararg elements: T): ObservableList<T> = FXCollections.observableArrayList(elements.toList())

fun onExit(sent: ObservableList<ProgressiveModel<File>>, downloaded: ObservableList<ProgressiveModel<File>>, event: WindowEvent) {
    var wait = false

    sent.forEach { if (it.state == ProgressiveModel.FileState.SENDING) wait = true }

    downloaded.forEach { if (it.state == ProgressiveModel.FileState.RECEIVING) wait = true }

    if (wait) {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.headerText = "You still have transfers in process."

        val buttonTypeWait = ButtonType("Wait")
        val buttonTypeExit = ButtonType("Exit")

        alert.buttonTypes.setAll(buttonTypeWait, buttonTypeExit)

        val result = alert.showAndWait()
        if (result.get() == buttonTypeWait) {
            event.consume()
        } else {
            System.exit(1)
        }

    } else System.exit(1)

}