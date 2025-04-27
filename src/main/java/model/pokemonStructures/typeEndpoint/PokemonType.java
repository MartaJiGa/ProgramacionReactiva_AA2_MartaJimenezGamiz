package model.pokemonStructures.typeEndpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PokemonType {
    private List<PokemonInfo> pokemon;
}