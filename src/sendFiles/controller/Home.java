package sendFiles.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
import sendFiles.model.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Home implements Initializable {


    @FXML
    private ScrollPane dragID;
    @FXML
    private VBox sendBoxID;
    @FXML
    private Label ipID;
    @FXML
    private Circle sendID;
    @FXML
    private TextField hostID;

    private List<File> files;

    public static ObservableList<File> listFiles = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ipID.setText(Utils.myIP());
        } catch (IOException e) {
            System.err.println("No se ha podido establecer la IP.");
        }

        dragID.setOnDragOver(this::mouseDragOver);

        dragID.setOnDragDropped(this::mouseDragDropped);

        dragID.setOnDragExited(event -> dragID.setEffect(null));

        sendID.setOnMouseClicked(event -> {

            for (File file : files) {
                try {
                    Client client = new Client("127.0.0.1", Integer.parseInt(hostID.getText()));
                    client.send(file.getAbsoluteFile().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            listFiles.addAll(files);
            files.clear();
            sendBoxID.getChildren().clear();
        });
    }

    private void mouseDragDropped(final DragEvent e) {
        final Dragboard db = e.getDragboard();

        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            files = db.getFiles();
            for (File file : files) {
                SingleFile singleFile = new SingleFile(file.getName());
                singleFile.getCloseButton().setOnAction(event -> {
                    sendBoxID.getChildren().remove(singleFile);
                    files.remove(file);
                });
                sendBoxID.getChildren().add(0, singleFile);
            }

        }
        e.setDropCompleted(success);
        e.consume();
    }

    private void mouseDragOver(final DragEvent e) {
        final Dragboard db = e.getDragboard();

        if (db.hasFiles()) {
            InnerShadow innerShadow = new InnerShadow(BlurType.THREE_PASS_BOX, Color.web("#097ebd81"),
                    51.36, 0.41, 0.0, 0.0);
            innerShadow.setHeight(100);
            innerShadow.setWidth(100);
            dragID.setEffect(innerShadow);
            e.acceptTransferModes(TransferMode.COPY);
        } else {
            e.consume();
        }
    }
}
