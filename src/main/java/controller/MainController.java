package controller;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.pokemonStructures.abilityEndpoint.AbilityDetail;
import model.pokemonStructures.abilityEndpoint.NameInfo;
import model.pokemonStructures.pokemonEndpoint.AbilityInfo;
import model.pokemonStructures.pokemonEndpoint.Pokemon;
import model.pokemonStructures.typeEndpoint.PokemonInfo;
import model.pokemonStructures.typeEndpoint.PokemonType;
import model.viewStructures.TableViewDataStructure;
import model.viewStructures.TableViewRow;
import service.MainService;
import utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainController {

    @FXML
    private ComboBox pokemonTypeComboBox;
    @FXML
    private TableView<TableViewDataStructure> dataTable;
    @FXML
    private TableColumn<TableViewDataStructure, String> pokemonColumn;
    @FXML
    private TableColumn<TableViewDataStructure, String> englishAbilityColumn;
    @FXML
    private TableColumn<TableViewDataStructure, String> spanishAbilityColumn;
    @FXML
    private TableColumn<TableViewDataStructure, String> isHiddenAbilityColumn;

    private MainService service;
    private Gson gson;

    @FXML
    public void initialize() {
        service = new MainService();
        gson = new Gson();
        fillPokemonTypeComboBox();
        prepareTableView();
    }

    @FXML
    private void searchEvent() {
        try {
            String selectedType = pokemonTypeComboBox.getValue().toString();
            if (selectedType == null || selectedType.isEmpty()) return;

            String jsonData = service.getPokemonData("type/", Constants.POKEMON_TYPES.get(selectedType).toLowerCase());
            PokemonType pokemonType = gson.fromJson(jsonData, PokemonType.class);

            fillTableView(jsonData, pokemonType);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillPokemonTypeComboBox(){
        List<String> orderedPokemonTypes = new ArrayList<>(Constants.POKEMON_TYPES.keySet());
        Collections.sort(orderedPokemonTypes);
        pokemonTypeComboBox.getItems().addAll(orderedPokemonTypes);
    }

    private void prepareTableView(){
        pokemonColumn.setCellValueFactory(new PropertyValueFactory<>("pokemon"));
        englishAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("english"));
        spanishAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("spanish"));
        isHiddenAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("isHidden"));

        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void fillTableView(String jsonData, PokemonType pokemonType) throws IOException, InterruptedException {
        // Crear una lista para guardar los Pokémon y sus habilidades
        ObservableList<TableViewDataStructure> dataListForTableView = FXCollections.observableArrayList();

        // TODO: Quitar esta variable
        int i = 0;
        for (PokemonInfo pokemonInfo : pokemonType.getPokemon()){
            Pokemon pokemon = getPokemon(pokemonInfo);

            for (AbilityInfo abilityInfo : pokemon.getAbilities()) {
                TableViewRow tableViewRow = getTableViewRow(jsonData, abilityInfo, pokemon.getName());
                dataListForTableView.add(new TableViewDataStructure(tableViewRow.getPokemonName(), tableViewRow.getEnglishName(), tableViewRow.getSpanishName(), tableViewRow.getIsHiddenText()));
            }

            // TODO: Quitar esta comprobación y este i++
            i++;
            if (i >= 5) break;
        }

        dataTable.setItems(dataListForTableView);
    }

    private Pokemon getPokemon(PokemonInfo pokemonInfo) throws IOException, InterruptedException {
        String pokemonData = service.getPokemonData("pokemon/", pokemonInfo.getPokemon().getName());
        return gson.fromJson(pokemonData, Pokemon.class);
    }

    private TableViewRow getTableViewRow(String jsonData, AbilityInfo abilityInfo, String pokemonName) throws IOException, InterruptedException {
        String englishNameToSearch = abilityInfo.getAbility().getName();

        // Busco la traducción en inglés porque el nombre que me llega está completamente en minúsculas y no es tan presentable.
        String englishName = getTranslation(jsonData, englishNameToSearch, "en");
        String spanishName = getTranslation(jsonData, englishNameToSearch, "es");
        String isHiddenText = abilityInfo.isIs_hidden() ? "Sí" : "No";

        pokemonName = pokemonName.substring(0, 1).toUpperCase() + pokemonName.substring(1).toLowerCase();

        return new TableViewRow(pokemonName, englishName, spanishName, isHiddenText);
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