package sendFiles;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Windows extends BorderPane {

    @FXML
    private ImageView inboxID;

    @FXML
    private ImageView homeID;

    public Windows() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "view/root.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        Parent home = FXMLLoader.load(getClass().getResource("view/home2.fxml"));
        Parent inbox = FXMLLoader.load(getClass().getResource("view/inbox.fxml"));

        this.setCenter(home);

        homeID.setOnMouseClicked(event -> this.setCenter(home));
        inboxID.setOnMouseClicked(event -> this.setCenter(inbox));
    }

}
