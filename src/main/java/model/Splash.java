package model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Constants;

import java.io.IOException;

public class Splash {

    public void show(Stage stage) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/views/splashView.fxml"));
        Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.getIcons().add(Constants.getAppLogo());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }
}
