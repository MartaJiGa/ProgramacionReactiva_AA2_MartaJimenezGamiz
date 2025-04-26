package model.pokemonStructures.typeEndpoint;

import java.util.List;

public class PokemonType {
    private List<PokemonInfo> pokemon;

    public List<PokemonInfo> getPokemon() {
        return pokemon;
    }

    public void setPokemon(List<PokemonInfo> pokemon) {
        this.pokemon = pokemon;
    }
}