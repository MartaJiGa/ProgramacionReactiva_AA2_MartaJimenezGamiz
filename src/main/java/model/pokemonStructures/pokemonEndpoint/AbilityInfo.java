package model.pokemonStructures.pokemonEndpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbilityInfo {

    private Ability ability;
    private boolean is_hidden;
}
