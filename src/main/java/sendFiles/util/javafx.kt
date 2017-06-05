package sendFiles.util

import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * Created by David on 04/06/2017.
 */
fun <T> observableListOf(vararg elements: T): ObservableList<T> = FXCollections.observableArrayList(elements.toList())
