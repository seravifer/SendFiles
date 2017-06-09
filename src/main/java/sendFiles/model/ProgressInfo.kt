package sendFiles.model

import tornadofx.*

abstract class ProgressInfo<T>(initialValue: T) : Scope() {
    var progress by property<Number>(0.0)
    fun progressProperty() = getProperty(ProgressInfo<T>::progress)
    var value by property(initialValue)
    fun valueProperty() = getProperty(ProgressInfo<T>::value)
    var state by property(FileTransferInfo.FileState.READY)
    fun stateProperty() = getProperty(ProgressInfo<T>::state)


}