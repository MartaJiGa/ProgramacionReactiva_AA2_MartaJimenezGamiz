package model.viewStructures;

import javafx.beans.property.SimpleStringProperty;

public class AbilitiesInTableView {
    private SimpleStringProperty english;
    private SimpleStringProperty spanish;
    private SimpleStringProperty isHidden;

    public AbilitiesInTableView(String english, String spanish, String isHidden) {
        this.english = new SimpleStringProperty(english);
        this.spanish = new SimpleStringProperty(spanish);
        this.isHidden = new SimpleStringProperty(isHidden);
    }

    public String getEnglish() {
        return english.get();
    }

    public SimpleStringProperty englishProperty() {
        return english;
    }

    public String getSpanish() {
        return spanish.get();
    }

    public SimpleStringProperty spanishProperty() {
        return spanish;
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
