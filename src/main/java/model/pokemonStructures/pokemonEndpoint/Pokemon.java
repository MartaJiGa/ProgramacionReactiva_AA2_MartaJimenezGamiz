package model.pokemonStructures.pokemonEndpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pokemon {
    private String name;
    private List<AbilityInfo> abilities;
}
