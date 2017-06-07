package sendFiles.view

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import sendFiles.Main
import sendFiles.controller.MainController
import tornadofx.*

class Root : View("SendFiles") {
    val controller = find<MainController>()
    val homeComponent by inject<Home>()
    val inboxComponent by inject<Inbox>()
    val outboxComponent by inject<Outbox>()
    val settingComponent by inject<Setting>()

    override val root: BorderPane by fxml()

    val settingsButtonID by fxid<ImageView>()
    val inboxButtonID by fxid<ImageView>()
    val outboxButtonID by fxid<ImageView>()
    val homeButtonID by fxid<ImageView>()

    init {
        primaryStage.icons.addAll(Image(Main::class.java.getResourceAsStream("icon/mipmap-hdpi/icon.png")),
                                  Image(Main::class.java.getResourceAsStream("icon/mipmap-mdpi/icon.png")),
                                  Image(Main::class.java.getResourceAsStream("icon/mipmap-xhdpi/icon.png")))
        primaryStage.minHeight = 500.0
        primaryStage.minWidth = 455.0

        root.center = homeComponent.root
        homeButtonID.setOnMouseClicked { root.center = homeComponent.root; setActive(homeButtonID) }
        inboxButtonID.setOnMouseClicked { root.center = inboxComponent.root; setActive(inboxButtonID) }
        outboxButtonID.setOnMouseClicked { root.center = outboxComponent.root; setActive(outboxButtonID) }
        settingsButtonID.setOnMouseClicked { root.center = settingComponent.root; setActive(settingsButtonID) }
    }

    fun setActive(button: ImageView) {
        root.left.getChildList()?.forEach { it.styleClass.remove("active") }
        button.styleClass.add("active")
    }

    override fun onDelete() {
        controller.close()
        super.onDelete()
    }
}