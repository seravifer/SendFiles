package sendFiles;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class Framework extends BorderPane {

    @FXML
    private ImageView settingsButtonID;

    @FXML
    private ImageView inboxButtonID;

    @FXML
    private ImageView outboxButtonID;

    @FXML
    private ImageView homeButtonID;

    public Framework() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/root.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Parent home = FXMLLoader.load(getClass().getResource("view/home.fxml"));
        Parent inbox = FXMLLoader.load(getClass().getResource("view/inbox.fxml"));
        Parent outbox = FXMLLoader.load(getClass().getResource("view/outbox.fxml"));

        this.setCenter(home);

        homeButtonID.setOnMouseClicked(event -> this.setCenter(home));
        inboxButtonID.setOnMouseClicked(event -> this.setCenter(inbox));
        outboxButtonID.setOnMouseClicked(event -> this.setCenter(outbox));
    }

}
