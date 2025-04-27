package model.pokemonStructures.abilityEndpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbilityDetail {
    private List<NameInfo> names;
}