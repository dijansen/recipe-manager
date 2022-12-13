package nl.dionjansen.recipemanager.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RecipeDto {

    private UUID id;

    @NotEmpty
    private String name;

    @NotNull
    private boolean vegetarian;

    @NotNull
    private Integer noOfServings;

    private List<String> ingredients;

    private List<String> instructions;
}
