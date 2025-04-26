package model.viewStructures;

import javafx.beans.property.SimpleStringProperty;

public class TableViewDataStructure {
    private SimpleStringProperty pokemon;
    private SimpleStringProperty english;
    private SimpleStringProperty spanish;
    private SimpleStringProperty isHidden;

    public TableViewDataStructure(String pokemon, String english, String spanish, String isHidden) {
        this.pokemon = new SimpleStringProperty(pokemon);
        this.english = new SimpleStringProperty(english);
        this.spanish = new SimpleStringProperty(spanish);
        this.isHidden = new SimpleStringProperty(isHidden);
    }

    public String getPokemon() {
        return pokemon.get();
    }

    public SimpleStringProperty pokemonProperty() {
        return pokemon;
    }

    public void setPokemon(String pokemon) {
        this.pokemon.set(pokemon);
    }

    public String getEnglish() {
        return english.get();
    }

    public SimpleStringProperty englishProperty() {
        return english;
    }

    public void setEnglish(String english) {
        this.english.set(english);
    }

    public String getSpanish() {
        return spanish.get();
    }

    public SimpleStringProperty spanishProperty() {
        return spanish;
    }

    public void setSpanish(String spanish) {
        this.spanish.set(spanish);
    }

    public String getIsHidden() {
        return isHidden.get();
    }

    public SimpleStringProperty isHiddenProperty() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden.set(isHidden);
    }
}