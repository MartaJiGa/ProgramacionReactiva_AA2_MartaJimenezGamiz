package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
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

import java.io.IOException;

public class MainService {

    private Retrofit retrofit;
    private PokemonAPI pokemonAPI;
    private OkHttpClient client;
    private CompositeDisposable disposables = new CompositeDisposable();

    public MainService() {
        client = new OkHttpClient.Builder()
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

    public void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    public void shutdown() {
        if (disposables != null && !disposables.isDisposed()) {
            disposables.dispose();
        }
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        if (client.cache() != null) {
            try {
                client.cache().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public Observable<Pokemon> getPokemonImageData(String pokemonName) {
        return pokemonAPI.getPokemonData(pokemonName);
    }
}