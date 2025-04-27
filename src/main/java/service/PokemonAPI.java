package service;

import io.reactivex.rxjava3.core.Observable;
import model.pokemonStructures.abilityEndpoint.AbilityDetail;
import model.pokemonStructures.pokemonEndpoint.Pokemon;
import model.pokemonStructures.typeEndpoint.PokemonType;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokemonAPI {
    @GET("type/{typeName}")
    Observable<PokemonType> getPokemonTypeData(@Path("typeName") String typeName);

    @GET("pokemon/{pokemonName}")
    Observable<Pokemon> getPokemonData(@Path("pokemonName") String pokemonName);

    @GET("ability/{abilityName}")
    Observable<AbilityDetail> getAbilityData(@Path("abilityName") String abilityName);
}