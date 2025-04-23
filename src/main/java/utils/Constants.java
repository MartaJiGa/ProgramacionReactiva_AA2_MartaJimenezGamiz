package utils;

import javafx.scene.image.Image;

public class Constants {
    public static final String APP_LOGO_PATH = "/appImages/appLogo.png";
    public static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2/";

    public static Image getAppLogo() {
        return new Image(APP_LOGO_PATH);
    }
}
