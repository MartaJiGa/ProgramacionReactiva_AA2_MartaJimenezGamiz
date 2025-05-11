package controller;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.pokemonStructures.abilityEndpoint.NameInfo;
import model.viewStructures.TableViewDataStructure;
import model.viewStructures.TableViewRow;
import service.MainService;
import utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

    @FXML
    private SplitPane mainPane;
    @FXML
    private ComboBox pokemonTypeComboBox;
    @FXML
    private TextField textSearch;
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

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @FXML
    public void initialize() {
        service = new MainService();
        fillPokemonTypeComboBox();
        configureFilterListeners();

        Platform.runLater(() -> {
            mainPane.getScene().getWindow().setOnCloseRequest(event -> {
                if (service != null) {
                    service.shutdown();
                }
                if (executorService != null && !executorService.isShutdown()) {
                    executorService.shutdown();
                }
            });
        });
    }

    //region EVENTS

    @FXML
    private void getPokemonDataEvent() {
        try {
            String selectedPokemonType = pokemonTypeComboBox.getValue().toString();
            if (selectedPokemonType == null || selectedPokemonType.isEmpty()) return;

            // Si la pestaña para ese tipo de Pokémon ya existe, no hacer nada
            if (checkIfTabExists(selectedPokemonType)) return;

            manageTask(selectedPokemonType);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchWordEvent() {
        try {
            String searchWord = textSearch.getText();
            if(searchWord.isEmpty()) return;

            String resultMessage = "«" + searchWord + "» se encuentra en:\n";
            String lowerCaseWord = textSearch.getText().toLowerCase();

            boolean found = false;

            for (Tab tab : tabPane.getTabs()) {
                VBox vbox = (VBox) tab.getContent();
                TableView<TableViewDataStructure> tableView = (TableView<TableViewDataStructure>) vbox.getChildren().get(1);

                ObservableList<TableViewDataStructure> tableData = tableView.getItems();
                for (TableViewDataStructure row : tableData) {
                    if (row.getPokemon().toLowerCase().contains(lowerCaseWord) ||
                            row.getEnglish().toLowerCase().contains(lowerCaseWord) ||
                            row.getSpanish().toLowerCase().contains(lowerCaseWord) ||
                            row.getIsHidden().toLowerCase().contains(lowerCaseWord)) {

                        resultMessage += "\t- " + tab.getText() + "\n";
                        found = true;
                        break;
                    }
                }
            }

            if (found) showSearchPopup(resultMessage.toString());
            else showSearchPopup("No se ha encontrado el término «" + searchWord + "».");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportCsvEvent(){
        try {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab == null) {
                showSearchPopup("No hay ninguna pestaña seleccionada.");
                return;
            }

            String tabName = selectedTab.getText();
            VBox vbox = (VBox) selectedTab.getContent();
            TableView<TableViewDataStructure> tableView = (TableView<TableViewDataStructure>) vbox.getChildren().get(1);
            ObservableList<TableViewDataStructure> items = tableView.getItems();

            if (items.isEmpty()) {
                showSearchPopup("No hay datos para exportar.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar CSV");
            fileChooser.setInitialFileName(tabName + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(tabPane.getScene().getWindow());
            if (file != null){
                try (OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8)) {

                    // Para que los datos que se guarden en el CSV aparezcan en UTF-8:
                    writer.write('\uFEFF');

                    writer.write("Pokémon;Habilidad (ing.);Habilidad (esp.);Habilidad oculta\n");

                    for (TableViewDataStructure row : items) {
                        String line = String.join(";", List.of(
                                row.getPokemon(),
                                row.getEnglish(),
                                row.getSpanish(),
                                row.getIsHidden()
                        ));
                        writer.write(line + "\n");
                    }
                    writer.flush();
                    showSearchPopup("CSV exportado correctamente en:\n" + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSearchPopup("Error al exportar CSV: " + e.getMessage());
        }
    }

    //endregion

    //region METHODS

    private void showSearchPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resultados de la búsqueda");
        alert.setContentText(message);
        alert.showAndWait();
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

    private void manageTask(String selectedPokemonType) {
        Task<Tab> loadDataTask = new Task<>() {
            @Override
            protected Tab call() throws Exception {
                return createTabForPokemonType(selectedPokemonType);
            }
        };

        loadDataTask.setOnSucceeded(workerStateEvent -> {
            Tab tab = loadDataTask.getValue();
            tabPane.getTabs().add(tab);
        });

        loadDataTask.setOnFailed(workerStateEvent -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al cargar datos");
            alert.setHeaderText("No se pudo cargar el tipo de Pokémon: " + selectedPokemonType);
            alert.setContentText(loadDataTask.getException().getMessage());
            alert.showAndWait();
        });

        executorService.submit(loadDataTask);
    }

    private Tab createTabForPokemonType(String pokemonTypeTab) {
        Tab tab = new Tab(pokemonTypeTab);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0);
        Label progressLabel = new Label();
        progressLabel.setText("0%");

        HBox progressBox = new HBox(10, progressBar, progressLabel);
        progressBox.setPadding(new Insets(5));

        TableView<TableViewDataStructure> pokemonTypeTableView = new TableView<>();
        prepareTableColumns(pokemonTypeTableView);

        ObservableList<TableViewDataStructure> pokemonTypeDataList = FXCollections.observableArrayList();

        loadPokemonData(pokemonTypeTab, pokemonTypeDataList, progressBar, progressLabel);

        pokemonTypeTableView.setItems(pokemonTypeDataList);
        tab.setUserData(pokemonTypeDataList);

        VBox vbox = new VBox(10);
        VBox.setVgrow(pokemonTypeTableView, Priority.ALWAYS);

        vbox.getChildren().add(progressBox);
        vbox.getChildren().add(pokemonTypeTableView);
        tab.setContent(vbox);

        return tab;
    }

    private void prepareTableColumns(TableView<TableViewDataStructure> tableView) {
        TableColumn<TableViewDataStructure, String> pokemonColumn = new TableColumn<>("Pokémon");
        TableColumn<TableViewDataStructure, String> englishAbilityColumn = new TableColumn<>("Habilidad (ing.)");
        TableColumn<TableViewDataStructure, String> spanishAbilityColumn = new TableColumn<>("Habilidad (esp.)");
        TableColumn<TableViewDataStructure, String> isHiddenAbilityColumn = new TableColumn<>("Habilidad oculta");
        TableColumn<TableViewDataStructure, ImageView> pokemonImageColumn = new TableColumn<>("Imagen");

        tableView.getColumns().addAll(pokemonColumn, englishAbilityColumn, spanishAbilityColumn, isHiddenAbilityColumn, pokemonImageColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        pokemonColumn.setCellValueFactory(new PropertyValueFactory<>("pokemon"));
        englishAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("english"));
        spanishAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("spanish"));
        isHiddenAbilityColumn.setCellValueFactory(new PropertyValueFactory<>("isHidden"));
        pokemonImageColumn.setCellValueFactory(new PropertyValueFactory<>("pokemonImage"));
    }

    private void loadPokemonData(String pokemonTypeTab, ObservableList<TableViewDataStructure> pokemonTypeDataList, ProgressBar progressBar, Label progressLabel) {
        Observable<List<TableViewRow>> pokemonObservable = service.getPokemonName(Constants.POKEMON_TYPES.get(pokemonTypeTab).toLowerCase())
                .toList()
                .flatMapObservable(pokemonList -> {
                    int total = pokemonList.size();
                    if (total == 0) return Observable.empty();

                    final int[] count = {0};

                    return Observable.fromIterable(pokemonList)
                            .concatMap(pokemonData ->
                                    service.getPokemonAbility(pokemonData.getName())
                                            .concatMap(abilityInfo ->
                                                    service.getPokemonAbilityTranslation(abilityInfo.getAbility().getName())
                                                            .toList()
                                                            .map(translations -> new TableViewRow(pokemonData.getName(), abilityInfo, translations))
                                                            .toObservable()
                                            )
                                            .toList()
                                            .toObservable()
                                            .doOnNext(rows -> Platform.runLater(() -> {
                                                for (TableViewRow row : rows) {
                                                    processAbilityInfo(row, pokemonTypeDataList);
                                                }

                                                count[0]++;
                                                double progress = (double) count[0] / total;
                                                progressBar.setProgress(progress);
                                                progressLabel.setText(String.format("%.0f%%", progress * 100));
                                            }))
                            );
                });

        Disposable disposable = pokemonObservable
                .subscribe(
                        item -> {},
                        this::manageError
                );

        service.addDisposable(disposable);
    }

    private void processAbilityInfo(TableViewRow tableViewRow, ObservableList<TableViewDataStructure> pokemonTypeDataList) {
        String englishName = getTranslationName(tableViewRow.getTranslations(), "en");
        String spanishName = getTranslationName(tableViewRow.getTranslations(), "es");
        String isHiddenText = tableViewRow.getAbilityInfo().is_hidden() ? "Sí" : "No";

        String formattedPokemonName = tableViewRow.getPokemonName().substring(0, 1).toUpperCase() + tableViewRow.getPokemonName().substring(1).toLowerCase();

        service.getPokemonImageData(tableViewRow.getPokemonName())
                .subscribe(pokemon -> {
                    String imageUrl = pokemon.getSprites().getImageType().getOfficialArtwork().getFrontDefault();
                    ImageView imageView;

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        ImageView imageViewWithUrl = new ImageView(new Image(imageUrl));
                        imageView = createPokemonImageView(imageViewWithUrl);
                    } else {
                        ImageView imageViewNoUrl = new ImageView();
                        imageView = createPokemonImageView(imageViewNoUrl);
                    }

                    Platform.runLater(() -> pokemonTypeDataList.add(new TableViewDataStructure(
                            formattedPokemonName,
                            englishName,
                            spanishName,
                            isHiddenText,
                            imageView
                    )));
                }, this::manageError);
    }

    private ImageView createPokemonImageView(ImageView imageView) {
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        return imageView;
    }

    private void manageError(Throwable error) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Ha habido un error al obtener los datos");
            alert.setContentText(error.getMessage());
            alert.showAndWait();
        });
    }

    private String getTranslationName(List<NameInfo> translations, String lang) {
        return translations.stream()
                .filter(nameInfo -> nameInfo.getLanguage().getName().equals(lang))
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
            TableView<TableViewDataStructure> tableView = (TableView<TableViewDataStructure>) vbox.getChildren().get(1);

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

    //endregion
}