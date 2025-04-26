package utils;

import javafx.scene.image.Image;

import java.util.Map;

public class Constants {
    public static final String APP_LOGO_PATH = "/appImages/appLogo.png";
    public static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2/";
    public static final Map<String, String> POKEMON_TYPES = Map.ofEntries(
            Map.entry("Acero", "Steel"),
            Map.entry("Agua", "Water"),
            Map.entry("Bicho", "Bug"),
            Map.entry("Dragón", "Dragon"),
            Map.entry("Eléctrico", "Electric"),
            Map.entry("Fantasma", "Ghost"),
            Map.entry("Fuego", "Fire"),
            Map.entry("Hada", "Fairy"),
            Map.entry("Hielo", "Ice"),
            Map.entry("Lucha", "Fighting"),
            Map.entry("Normal", "Normal"),
            Map.entry("Planta", "Grass"),
            Map.entry("Psíquico", "Psychic"),
            Map.entry("Roca", "Rock"),
            Map.entry("Siniestro", "Dark"),
            Map.entry("Tierra", "Ground"),
            Map.entry("Veneno", "Poison"),
            Map.entry("Volador", "Flying")
    );

    public static Image getAppLogo() {
        return new Image(APP_LOGO_PATH);
    }
}
