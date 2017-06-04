package sendFiles.controller;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import sendFiles.component.SingleFile;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Outbox implements Initializable {

    @FXML
    private VBox outboxID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Home.listFiles.addListener((ListChangeListener<File>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (File item : c.getAddedSubList()) {
                        outboxID.getChildren().add(0, new SingleFile(item.getName()));
                    }
                }
            }
        });
    }

}
