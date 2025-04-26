package model.viewStructures;

public class TableViewRow {
    private String pokemonName;
    private String englishName;
    private String spanishName;
    private String isHiddenText;

    public TableViewRow(String pokemonName, String englishName, String spanishName, String isHiddenText) {
        this.pokemonName = pokemonName;
        this.englishName = englishName;
        this.spanishName = spanishName;
        this.isHiddenText = isHiddenText;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getSpanishName() {
        return spanishName;
    }

    public void setSpanishName(String spanishName) {
        this.spanishName = spanishName;
    }

    public String getIsHiddenText() {
        return isHiddenText;
    }

    public void setIsHiddenText(String isHiddenText) {
        this.isHiddenText = isHiddenText;
    }
}
