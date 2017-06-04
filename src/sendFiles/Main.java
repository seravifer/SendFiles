package sendFiles;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        /*root = FXMLLoader.load(getClass().getResource("view/root.fxml"));
        home = FXMLLoader.load(getClass().getResource("view/home2.fxml"));
        inbox = FXMLLoader.load(getClass().getResource("view/inbox.fxml"));*/

        Windows windows = new Windows();
        Scene scene = new Scene(windows);
        scene.getStylesheets().add(getClass().getResource("res/style.css").toString());

        stage.setScene(scene);
        stage.setTitle("Send Files");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
