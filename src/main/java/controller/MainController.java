package controller;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
    private TabPane tabPane;
    @FXML
    private TextField pokemonFilterTextField;
    @FXML
    private TextField englishAbilityFilterTextField;
    @FXML
    private TextField spanishAbilityFilterTextField;
    @FXML
    private TextField isHiddenFilterTextField;

    private MainService service;
    private Gson gson;

    @FXML
    public void initialize() {
        service = new MainService();
        gson = new Gson();
        fillPokemonTypeComboBox();
        configureFilterListeners();
    }

    @FXML
    private void searchEvent() {
        try {
            String selectedPokemonType = pokemonTypeComboBox.getValue().toString();
            if (selectedPokemonType == null || selectedPokemonType.isEmpty()) return;

            // Si la pestaña para ese tipo de Pokémon ya existe, no hacer nada
            if (checkIfTabExists(selectedPokemonType)) return;

            Tab newTab = createTabForPokemonType(selectedPokemonType);
            tabPane.getTabs().add(newTab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillPokemonTypeComboBox(){
        List<String> orderedPokemonTypes = new ArrayList<>(Constants.POKEMON_TYPES.keySet());
        Collections.sort(orderedPokemonTypes);
        pokemonTypeComboBox.getItems().addAll(orderedPokemonTypes);
    }

    private void configureFilterListeners() {
        pokemonFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        englishAbilityFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        spanishAbilityFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        isHiddenFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
    }

    private boolean checkIfTabExists(String pokemonType) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(pokemonType)) {
                return true;
            }
        }
        return false;
    }

    private Tab createTabForPokemonType(String pokemonTypeTab) throws IOException, InterruptedException {
        Tab tab = new Tab(pokemonTypeTab);

        TableView<TableViewDataStructure> pokemonTypeTableView = new TableView<>();
        prepareTableColumns(pokemonTypeTableView);

        String jsonData = service.getPokemonData("type/", Constants.POKEMON_TYPES.get(pokemonTypeTab).toLowerCase());
        PokemonType pokemonType = gson.fromJson(jsonData, PokemonType.class);
        ObservableList<TableViewDataStructure> pokemonTypeDataList = FXCollections.observableArrayList();

        fillTableWithPokemonData(pokemonType, pokemonTypeDataList);
        pokemonTypeTableView.setItems(pokemonTypeDataList);

        tab.setUserData(pokemonTypeDataList);

        VBox vbox = new VBox();
        VBox.setVgrow(pokemonTypeTableView, Priority.ALWAYS);
        vbox.getChildren().add(pokemonTypeTableView);
        tab.setContent(vbox);

        return tab;
    }

    private void prepareTableColumns(TableView<TableViewDataStructure> tableView) {
        TableColumn<TableViewDataStructure, String> pokemonColumn = new TableColumn<>("Pokémon");
        TableColumn<TableViewDataStructure, String> englishAbilityColumn = new TableColumn<>("Habilidad (ing.)");
        TableColumn<TableViewDataStructure, String> spanishAbilityColumn = new TableColumn<>("Habilidad (esp.)");
        TableColumn<TableViewDataStructure, String> isHiddenAbilityColumn = new TableColumn<>("Habilidad oculta");

        tableView.getColumns().addAll(pokemonColumn, englishAbilityColumn, spanishAbilityColumn, isHiddenAbilityColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        pokemonColumn.setCellValueFactory(new PropertyValueFactory<>("pokemon"));
        englishAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("english"));
        spanishAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("spanish"));
        isHiddenAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("isHidden"));
    }

    private void fillTableWithPokemonData(PokemonType pokemonType, ObservableList<TableViewDataStructure> dataList) throws IOException, InterruptedException {
        for (PokemonInfo pokemonInfo : pokemonType.getPokemon()) {
            Pokemon pokemon = getPokemon(pokemonInfo);

            for (AbilityInfo abilityInfo : pokemon.getAbilities()) {
                TableViewRow tableViewRow = getTableViewRow(abilityInfo, pokemon.getName());
                dataList.add(new TableViewDataStructure(tableViewRow.getPokemonName(), tableViewRow.getEnglishName(), tableViewRow.getSpanishName(), tableViewRow.getIsHiddenText()));
            }
        }
    }

    private Pokemon getPokemon(PokemonInfo pokemonInfo) throws IOException, InterruptedException {
        String pokemonData = service.getPokemonData("pokemon/", pokemonInfo.getPokemon().getName());
        return gson.fromJson(pokemonData, Pokemon.class);
    }

    private TableViewRow getTableViewRow(AbilityInfo abilityInfo, String pokemonName) throws IOException, InterruptedException {
        String englishNameToSearch = abilityInfo.getAbility().getName();
        String englishName = getAbilityTranslation(englishNameToSearch, "en");
        String spanishName = getAbilityTranslation(englishNameToSearch, "es");
        String isHiddenText = abilityInfo.isIs_hidden() ? "Sí" : "No";

        pokemonName = pokemonName.substring(0, 1).toUpperCase() + pokemonName.substring(1).toLowerCase();

        return new TableViewRow(pokemonName, englishName, spanishName, isHiddenText);
    }

    private String getAbilityTranslation(String abilityName, String language) throws IOException, InterruptedException {
        String jsonData = service.getPokemonData("ability/", abilityName);
        AbilityDetail abilityDetail = gson.fromJson(jsonData, AbilityDetail.class);
        return abilityDetail.getNames().stream()
                .filter(nameInfo -> language.equals(nameInfo.getLanguage().getName()))
                .map(NameInfo::getName)
                .findFirst()
                .orElse("No encontrado");
    }

    private void filterTable() {
        String pokemonFilter = pokemonFilterTextField.getText().toLowerCase();
        String englishAbilityFilter = englishAbilityFilterTextField.getText().toLowerCase();
        String spanishAbilityFilter = spanishAbilityFilterTextField.getText().toLowerCase();
        String isHiddenFilter = isHiddenFilterTextField.getText().toLowerCase();

        for (Tab tab : tabPane.getTabs()) {
            VBox vbox = (VBox) tab.getContent();
            TableView<TableViewDataStructure> tableView = (TableView<TableViewDataStructure>) vbox.getChildren().get(0);

            ObservableList<TableViewDataStructure> originalList = (ObservableList<TableViewDataStructure>) tab.getUserData();
            if (originalList == null) continue;

            ObservableList<TableViewDataStructure> filteredList = FXCollections.observableArrayList();

            for (TableViewDataStructure item : originalList) {
                boolean matches = true;

                if (!pokemonFilter.isEmpty() && !item.getPokemon().toLowerCase().contains(pokemonFilter)) {
                    matches = false;
                }
                if (!englishAbilityFilter.isEmpty() && !item.getEnglish().toLowerCase().contains(englishAbilityFilter)) {
                    matches = false;
                }
                if (!spanishAbilityFilter.isEmpty() && !item.getSpanish().toLowerCase().contains(spanishAbilityFilter)) {
                    matches = false;
                }
                if (!isHiddenFilter.isEmpty() && !item.getIsHidden().toLowerCase().contains(isHiddenFilter)) {
                    matches = false;
                }

                if (matches) filteredList.add(item);
            }
            tableView.setItems(filteredList);
        }
    }
}