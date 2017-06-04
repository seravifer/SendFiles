package shared.component;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SingleFile extends AnchorPane {

    @FXML
    private Label nameID;

    @FXML
    private ProgressBar progressID;

    @FXML
    private Label sizeID;

    public SingleFile(Task<String> item) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SingleFile.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            System.err.println("Error al cargar la vista: " + this.getClass().getSimpleName());
        }

        nameID.textProperty().bind(item.valueProperty());
        progressID.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                progressID.setVisible(false);
            }
        });

        progressID.progressProperty().bind(item.progressProperty());

    }
}
