package sendFiles.view

import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.effect.BlurType
import javafx.scene.effect.InnerShadow
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import sendFiles.component.HomeFileListFragment
import sendFiles.controller.MainController
import sendFiles.model.ProgressiveModel
import sendFiles.util.getIpAddress
import sendFiles.util.observableListOf
import tornadofx.*
import java.io.File

/**
 * Created by David on 05/06/2017.
 */
class Home : View() {
    private val controller by inject<MainController>()

    override val root by fxml<AnchorPane>()

    val dragID by fxid<AnchorPane>()
    val sendBoxID by fxid<ListView<ProgressiveModel<File>>>()
    val ipID by fxid<Label>()
    val sendID by fxid<StackPane>()
    val hostID by fxid<TextField>()

    var files = observableListOf<ProgressiveModel<File>>()

    init {
        ipID.text = getIpAddress()

        dragID.setOnDragOver(this::mouseDragOver)
        dragID.setOnDragDropped(this::mouseDragDropped)
        dragID.setOnDragExited { dragID.effect = null }

        sendBoxID.items = files
        sendBoxID.cellFragment(HomeFileListFragment::class)

        sendID.setOnMouseClicked {
            try {
                controller.send(files.map { it.value }, "localhost", Integer.parseInt(hostID.text))
                files.clear()
            } catch(e: NumberFormatException) {
                tornadofx.error("Puerto inválido", "El puerto seleccionado no es un número")
            }
        }
    }

    private fun mouseDragDropped(e: DragEvent) {
        val db = e.dragboard
        e.isDropCompleted = if (db.hasFiles()) {
            files.addAll(db.files.map { ProgressiveModel<File>().apply { value = it } })
            true
        } else {
            false
        }
        e.consume()
    }

    private fun mouseDragOver(e: DragEvent) {
        val db = e.dragboard

        if (db.hasFiles()) {
            val innerShadow = InnerShadow(BlurType.THREE_PASS_BOX, Color.web("#097ebd81"), 51.36, 0.41, 0.0, 0.0)
            innerShadow.height = 100.0
            innerShadow.width = 100.0
            dragID.effect = innerShadow
            e.acceptTransferModes(TransferMode.COPY)
        } else {
            e.consume()
        }
    }
}
