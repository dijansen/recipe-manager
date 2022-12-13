package nl.dionjansen.recipemanager.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import nl.dionjansen.recipemanager.entities.QRecipe;
import nl.dionjansen.recipemanager.entities.Recipe;
import nl.dionjansen.recipemanager.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecipeService {

    private static final String INGREDIENTS = "ingredients";
    private static final String INSTRUCTIONS = "instructions";

    private static final String NO_OF_SERVINGS = "noOfServings";
    private static final String VEGETARIAN = "vegetarian";

    @Autowired
    private RecipeRepository repository;

    public List<Recipe> find(Map<String, String> parameters) {
        Iterable<Recipe> iterable = repository.findAll(getPredicate(parameters));
        List<Recipe> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    public Recipe create(Recipe recipe) {
        return repository.saveAndFlush(recipe);
    }

    public Recipe update(Recipe map) {
        return repository.saveAndFlush(map);
    }

    public void delete(UUID id) {
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
        }
    }

    private static Predicate getPredicate(Map<String, String> parameters) {
        QRecipe recipe = QRecipe.recipe;
        BooleanBuilder builder = new BooleanBuilder();
        // Just making sure predicate will not be null
        builder.and(recipe.id.isNotNull());
        // See if we are looking for veggy dishes
        if (parameters.containsKey(VEGETARIAN)) {
            builder.and(recipe.vegetarian.eq(Boolean.valueOf(parameters.get(VEGETARIAN))));
        }
        // Or certain amount of servings
        if (parameters.containsKey(NO_OF_SERVINGS)) {
            builder.and(recipe.noOfServings.eq(Integer.valueOf(parameters.get(NO_OF_SERVINGS))));
        }
        // Or certain ingredients
        if (parameters.containsKey(INGREDIENTS)) {
            for (String ingredient: parameters.get(INGREDIENTS).split(",")) {
                // We use '!' to indicate we DON'T want certain ingredients
                if (ingredient.startsWith("!")) {
                    builder.andNot(recipe.ingredients.any().containsIgnoreCase(ingredient.substring(1)));
                }
                else {
                    builder.and(recipe.ingredients.any().containsIgnoreCase(ingredient));
                }
            }
        }
        // Or some particular words in the instructions
        if (parameters.containsKey(INSTRUCTIONS)) {
            for (String instruction: parameters.get(INSTRUCTIONS).split(",")) {
                builder.and(recipe.instructions.any().containsIgnoreCase(instruction));
            }
        }
        return builder.getValue();
    }

    public Optional<Recipe> findById(UUID id) {
        return repository.findById(id);
    }
}
