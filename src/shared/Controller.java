package shared;

import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import shared.component.ListItems;
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
    private AnchorPane recibidosID;

    @FXML
    private Button sendID;

    @FXML
    private TextField hostID;

    @FXML
    private AnchorPane dragID;

    @FXML
    private Label ipID;

    @FXML
    private ListView<Task<String>> listaID;

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

        ListItems listItems = new ListItems();
        listItems.setItems(server.listFiles);

        AnchorPane.setBottomAnchor(listItems, 1.0);
        AnchorPane.setLeftAnchor(listItems, 1.0);
        AnchorPane.setRightAnchor(listItems, 1.0);
        AnchorPane.setTopAnchor(listItems, 1.0);
        recibidosID.getChildren().add(listItems);

        listaID.setItems(server.listFiles);
        listaID.setCellFactory(lista -> new ListCell<Task<String>>() {
            @Override
            protected void updateItem(Task<String> item, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(5);
                    Label label = new Label();
                    label.textProperty().bind(item.valueProperty());
                    ProgressBar progressBar = new ProgressBar(0);
                    progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0) {
                            progressBar.setVisible(false);
                        }
                    });
                    progressBar.progressProperty().bind(item.progressProperty());
                    hBox.getChildren().addAll(label, progressBar);
                    setGraphic(hBox);
                }

            }
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
}
