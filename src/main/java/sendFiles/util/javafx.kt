package sendFiles.util

import javafx.collections.FXCollections
import javafx.collections.ObservableList

fun <T> observableListOf(vararg elements: T): ObservableList<T> = FXCollections.observableArrayList(elements.toList())