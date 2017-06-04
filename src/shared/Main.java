package shared;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/home.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("res/style.css").toString());

        stage.setScene(scene);
        stage.setTitle("Send Files");
        stage.show();
    }
}
