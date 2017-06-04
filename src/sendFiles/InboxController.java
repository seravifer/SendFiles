package sendFiles;

import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sendFiles.component.SingleFile;
import sendFiles.model.Client;
import sendFiles.model.Server;
import sendFiles.model.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class InboxController implements Initializable {

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
