package model.viewStructures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.pokemonStructures.abilityEndpoint.NameInfo;
import model.pokemonStructures.pokemonEndpoint.AbilityInfo;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableViewRow {
    private String pokemonName;
    private AbilityInfo abilityInfo;
    private List<NameInfo> translations;
}
