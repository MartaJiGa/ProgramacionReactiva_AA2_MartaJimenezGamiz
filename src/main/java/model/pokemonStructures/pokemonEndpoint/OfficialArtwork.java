package model.pokemonStructures.pokemonEndpoint;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficialArtwork {
    @SerializedName("front_default")
    private String frontDefault;
}
