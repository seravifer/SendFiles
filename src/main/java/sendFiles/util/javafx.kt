package sendFiles.util

import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch

fun <T> observableListOf(vararg elements: T): ObservableList<T> = FXCollections.observableArrayList(elements.toList())

fun <T> ObservableValue<T>.addAsyncListener(listener: suspend (observable: ObservableValue<out T>, oldValue: T, newValue: T) -> Unit) {
    addListener { observable: ObservableValue<out T>, oldValue: T, newValue: T ->
        launch(JavaFx) {
            listener(observable, oldValue, newValue)
        }
    }
}