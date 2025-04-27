package model.pokemonStructures.abilityEndpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameInfo {
    private String name;
    private Language language;
}