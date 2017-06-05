package sendFiles

import sendFiles.view.Root
import tornadofx.*

/**
 * Created by David on 05/06/2017.
 */
class Main : App(Root::class) {
    init {
        importStylesheet("/sendFiles/res/style.css")
    }
}