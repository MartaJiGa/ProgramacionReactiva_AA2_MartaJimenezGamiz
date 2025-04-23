package controller;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.pokemonStructures.AbilityInfo;
import model.pokemonStructures.Pokemon;
import service.MainService;

public class MainController {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private ListView dataList;

    private MainService service;
    private Gson gson;

    @FXML
    public void initialize() {
        service = new MainService();
        gson = new Gson();
    }

    @FXML
    private void searchEvent() {
        try {
            String jsonData = service.getPokemonData(searchField.getText().toString().toLowerCase());

            Pokemon pokemon = gson.fromJson(jsonData, Pokemon.class);

            ObservableList<String> abilitiesList = FXCollections.observableArrayList();

            for (AbilityInfo abilityInfo : pokemon.getAbilities()) {
                abilitiesList.add(abilityInfo.getAbility().getName());
            }

            dataList.setItems(abilitiesList);

            searchField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            searchField.setText("");
        }
    }
}