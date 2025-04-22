package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private AnchorPane splashPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ShowSplash();
    }

    private void ShowSplash(){
        new Thread(){
            public void run(){
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            borderPane = FXMLLoader.load(getClass().getResource("/views/mainView.fxml"));

                            Stage stage = new Stage();
                            Scene scene = new Scene(borderPane);

                            stage.setTitle("Programación reactiva");
                            stage.setScene(scene);
                            stage.getIcons().add(Constants.getAppLogo());
                            stage.show();

                            splashPane.getScene().getWindow().hide();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }.start();
    }
}
