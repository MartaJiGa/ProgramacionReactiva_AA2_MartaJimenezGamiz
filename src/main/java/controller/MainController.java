package controller;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.pokemonStructures.abilityEndpoint.AbilityDetail;
import model.pokemonStructures.abilityEndpoint.NameInfo;
import model.pokemonStructures.pokemonEndpoint.AbilityInfo;
import model.pokemonStructures.pokemonEndpoint.Pokemon;
import model.viewStructures.AbilitiesInTableView;
import service.MainService;

import java.io.IOException;

public class MainController {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<AbilitiesInTableView> dataTable;
    @FXML
    private TableColumn<AbilitiesInTableView, String> englishColumn;
    @FXML
    private TableColumn<AbilitiesInTableView, String> spanishColumn;
    @FXML
    private TableColumn<AbilitiesInTableView, String> isHiddenColumn;

    private MainService service;
    private Gson gson;

    @FXML
    public void initialize() {
        service = new MainService();
        gson = new Gson();
        englishColumn.setCellValueFactory(new PropertyValueFactory<>("english"));
        spanishColumn.setCellValueFactory(new PropertyValueFactory<>("spanish"));
        isHiddenColumn.setCellValueFactory(new PropertyValueFactory<>("isHidden"));

        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void searchEvent() {
        try {
            String jsonData = service.getPokemonData("pokemon/", searchField.getText().toString().toLowerCase());

            Pokemon pokemon = gson.fromJson(jsonData, Pokemon.class);

            ObservableList<AbilitiesInTableView> abilitiesList = FXCollections.observableArrayList();

            for (AbilityInfo abilityInfo : pokemon.getAbilities()) {
                String englishNameToSearch = abilityInfo.getAbility().getName();

                // Busco la traducción en inglés porque el nombre que me llega está completamente en minúsculas y no es tan presentable.
                String englishName = getTranslation(jsonData, englishNameToSearch, "en");
                String spanishName = getTranslation(jsonData, englishNameToSearch, "es");
                String isHiddenText = abilityInfo.isIs_hidden() ? "Verdadero" : "Falso";

                abilitiesList.add(new AbilitiesInTableView(englishName, spanishName, isHiddenText));
            }

            dataTable.setItems(abilitiesList);

            searchField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            searchField.setText("");
        }
    }

    private String getTranslation(String jsonData, String englishNameToSearch, String language) throws IOException, InterruptedException {
        jsonData = service.getPokemonData("ability/", englishNameToSearch);
        AbilityDetail abilityDetail = gson.fromJson(jsonData, AbilityDetail.class);

        return abilityDetail.getNames().stream()
                .filter(nameInfo -> language.equals(nameInfo.getLanguage().getName()))
                .map(NameInfo::getName)
                .findFirst()
                .orElse("No encontrado");
    }
}