package sendFiles.model.network

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import sendFiles.model.network.event.AcceptDownload
import tornadofx.*

/**
 * Created by David on 08/06/2017.
 */
object NetworkHandler : Component() {
    fun init() {
        subscribe<AcceptDownload> {
            launch(CommonPool) {
                it.fileInfo.handler.requestFile(it.fileInfo)
            }
        }
    }
}