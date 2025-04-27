package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.rxjava3.core.Observable;
import model.pokemonStructures.abilityEndpoint.AbilityDetail;
import model.pokemonStructures.abilityEndpoint.NameInfo;
import model.pokemonStructures.pokemonEndpoint.AbilityInfo;
import model.pokemonStructures.pokemonEndpoint.Pokemon;
import model.pokemonStructures.typeEndpoint.PokemonData;
import model.pokemonStructures.typeEndpoint.PokemonInfo;
import model.pokemonStructures.typeEndpoint.PokemonType;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.Constants;

public class MainService {

    private Retrofit retrofit;
    private PokemonAPI pokemonAPI;

    public MainService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.POKEAPI_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        pokemonAPI = retrofit.create(PokemonAPI.class);
    }

    public Observable<PokemonData> getPokemonName(String word) {
        return pokemonAPI.getPokemonTypeData(word)
                .flatMapIterable(PokemonType::getPokemon)
                .map(PokemonInfo::getPokemon);
    }

    public Observable<AbilityInfo> getPokemonAbility(String pokemonName) {
        return pokemonAPI.getPokemonData(pokemonName)
                .flatMapIterable(Pokemon::getAbilities);
    }

    public Observable<NameInfo> getPokemonAbilityTranslation(String word) {
        return pokemonAPI.getAbilityData(word)
                .flatMapIterable(AbilityDetail::getNames)
                .filter(nameInfo -> {
                    String language = nameInfo.getLanguage().getName();
                    return language.equals("en") || language.equals("es");
                });
    }
}