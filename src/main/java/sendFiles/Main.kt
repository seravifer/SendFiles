package sendFiles

import sendFiles.view.Root
import tornadofx.*
import java.nio.file.Path

class Main : App(Root::class) {

    override val configPath: Path = configBasePath.resolve("setting.properties")

    init {
        importStylesheet("/sendFiles/style.css")
    }
}