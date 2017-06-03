package shared;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import shared.model.Client;
import shared.model.Server;
import shared.model.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button sendID;

    @FXML
    private TextField hostID;

    @FXML
    private AnchorPane dragID;

    @FXML
    private Label ipID;

    @FXML
    private Label statusID;

    private List<File> files;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Server server = new Server();
        server.start();

        try {
            ipID.setText(Utils.myIP());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dragID.setOnDragOver(this::mouseDragOver);

        dragID.setOnDragDropped(this::mouseDragDropped);

        dragID.setOnDragExited(event -> dragID.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false));

        sendID.setOnAction(event -> {

            for (File file : files) {
                try {
                    Client client = new Client("127.0.0.1", Integer.parseInt(hostID.getText()));
                    client.send(file.getAbsoluteFile().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            files.clear();
        });
    }

    private void mouseDragDropped(final DragEvent e) {
        final Dragboard db = e.getDragboard();

        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            files = db.getFiles();
        }
        e.setDropCompleted(success);
        e.consume();
    }

    private void mouseDragOver(final DragEvent e) {
        final Dragboard db = e.getDragboard();

        if (db.hasFiles()) {
            dragID.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
            e.acceptTransferModes(TransferMode.COPY);
        } else {
            e.consume();
        }
    }

    public void setStatus(String s) {
        statusID.setText(s);
    }
}
