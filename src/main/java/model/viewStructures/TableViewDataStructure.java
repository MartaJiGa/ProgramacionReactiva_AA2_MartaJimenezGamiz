package model.viewStructures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableViewDataStructure {
    private String pokemon;
    private String english;
    private String spanish;
    private String isHidden;
}