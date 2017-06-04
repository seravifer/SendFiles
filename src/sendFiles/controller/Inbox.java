package sendFiles.controller;

import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import sendFiles.component.SingleFile;
import sendFiles.model.Server;

import java.net.URL;
import java.util.ResourceBundle;

public class Inbox implements Initializable {

    @FXML
    private VBox inboxID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Server server = new Server();
        server.start();

        server.listFiles.addListener((ListChangeListener<Task<String>>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Task<String> additem : c.getAddedSubList()) {
                        inboxID.getChildren().add(0, new SingleFile(additem));
                    }
                }
            }
        });
    }

}
