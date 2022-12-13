package nl.dionjansen.recipemanager.controllers;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import nl.dionjansen.recipemanager.dtos.RecipeDto;
import nl.dionjansen.recipemanager.entities.Recipe;
import nl.dionjansen.recipemanager.services.RecipeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private RecipeService service;

    @PostMapping
    @Operation(summary = "Create recipe", description = "Create a recipe")
    public ResponseEntity post(@RequestBody RecipeDto dto) {
        return new ResponseEntity(map(service.create(map(dto))), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get recipe", description = "Get a specific recipe")
    public ResponseEntity get(@PathVariable UUID id) {
        Optional<Recipe> optional = service.findById(id);
        if (optional.isPresent()) {
            return ResponseEntity.ok(map(optional.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get recipes", description = "Get a list of available recipes", parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "vegetarian", required = false, description = "Indicator of dish to be vegetarian", example = "false"),
            @Parameter(in = ParameterIn.QUERY, name = "noOfServings", required = false, description = "Exact amount of servings from the recipe", example = "4"),
            @Parameter(in = ParameterIn.QUERY, name = "ingredients", required = false, description = "Comma separated list of ingredients that should be in the recipe. Use '!' as prefix to indicate an excluded ingredient from search", example = "pesto,!broccoli"),
            @Parameter(in = ParameterIn.QUERY, name = "instructions", required = false, description = "Comma separated list of terms that should occur in the instructions", example = "boil,taste"),
    })
    public ResponseEntity get(@RequestParam(required=false) Map<String,String> parameters) {
        return ResponseEntity.ok(service.find(parameters).stream().map(this::map));
    }

    @PutMapping("{id}")
    @Operation(summary = "Update recipe", description = "Update a specific recipe")
    public ResponseEntity put(@PathVariable UUID id, @RequestBody RecipeDto dto) {
        if (!id.equals(dto.getId())) {
            return ResponseEntity.badRequest().build();
        }
        service.update(map(dto));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete recipe", description = "Delete a specific recipe")
    public ResponseEntity delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    private Recipe map(RecipeDto recipeDto) {
        return mapper.map(recipeDto, Recipe.class);
    }

    private RecipeDto map(Recipe recipe) {
        return mapper.map(recipe, RecipeDto.class);
    }
}
