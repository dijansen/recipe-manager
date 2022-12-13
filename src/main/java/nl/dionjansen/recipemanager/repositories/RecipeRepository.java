package nl.dionjansen.recipemanager.repositories;

import nl.dionjansen.recipemanager.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID>, QuerydslPredicateExecutor {

}
