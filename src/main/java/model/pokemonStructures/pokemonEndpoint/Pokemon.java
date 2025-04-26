package model.pokemonStructures.pokemonEndpoint;

import java.util.List;

public class Pokemon {
    private String name;
    private List<AbilityInfo> abilities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AbilityInfo> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<AbilityInfo> abilities) {
        this.abilities = abilities;
    }
}
