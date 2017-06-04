package sendFiles.component;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ListItems extends ScrollPane {

    @FXML
    private VBox itemsID;

    public ListItems() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListItems.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            System.err.println("Error al cargar la vista: " + this.getClass().getSimpleName());
        }
    }

    public void setItems(ObservableList<Task<String>> observableList) {
        observableList.addListener((ListChangeListener<Task<String>>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Task<String> additem : c.getAddedSubList()) {
                        itemsID.getChildren().add(0, new SingleFile(additem));
                    }
                }
            }
        });

    }

}
