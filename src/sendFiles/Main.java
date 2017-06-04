package sendFiles;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Framework framework = new Framework();

        Scene scene = new Scene(framework);
        scene.getStylesheets().add(getClass().getResource("res/style.css").toString());

        stage.setScene(scene);
        stage.setTitle("Send Files");
        stage.setMinWidth(450);
        stage.show();
    }

}
