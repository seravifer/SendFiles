package sendFiles.model

import tornadofx.*

class ProgressiveModel<T>(initialValue: T? = null) : ItemViewModel<T>() {
    var progress by property<Number>(0.0)
    fun progressProperty() = getProperty(ProgressiveModel<T>::progress)
    var value by property(initialValue)
    fun valueProperty() = getProperty(ProgressiveModel<T>::value)
    var state by property(FileState.READY)
    fun stateProperty() = getProperty(ProgressiveModel<T>::state)

    enum class FileState {
        READY,
        SENDING,
        RECEIVING,
        FAILED,
        CANCELED,
        RECEIVED,
        SENT
    }
}