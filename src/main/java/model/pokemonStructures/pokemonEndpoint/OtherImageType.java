package model.pokemonStructures.pokemonEndpoint;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtherImageType {
    @SerializedName("official-artwork")
    private OfficialArtwork officialArtwork;
}
