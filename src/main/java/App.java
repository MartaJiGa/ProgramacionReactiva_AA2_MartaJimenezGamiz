import javafx.application.Application;
import javafx.stage.Stage;
import model.Splash;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        new Splash().show(stage);
    }
}
