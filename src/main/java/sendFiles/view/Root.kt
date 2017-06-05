package sendFiles.view

import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import sendFiles.controller.MainController
import tornadofx.*

/**
 * Created by David on 05/06/2017.
 */
class Root : View() {
    val controller = find<MainController>()
    val homeComponent by inject<Home>()
    val inboxComponent by inject<Inbox>()
    val outboxComponent by inject<Outbox>()

    override val root: BorderPane by fxml()

    val settingsButtonID by fxid<ImageView>()
    val inboxButtonID by fxid<ImageView>()
    val outboxButtonID by fxid<ImageView>()
    val homeButtonID by fxid<ImageView>()

    init {
        root.center = homeComponent.root
        homeButtonID.setOnMouseClicked { root.center = homeComponent.root }
        inboxButtonID.setOnMouseClicked { root.center = inboxComponent.root }
        outboxButtonID.setOnMouseClicked { root.center = outboxComponent.root }
    }

    override fun onDelete() {
        controller.close()
        super.onDelete()
    }
}
