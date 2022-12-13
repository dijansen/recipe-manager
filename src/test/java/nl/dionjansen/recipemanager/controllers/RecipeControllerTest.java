package nl.dionjansen.recipemanager.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dionjansen.recipemanager.dtos.RecipeDto;
import nl.dionjansen.recipemanager.entities.Recipe;
import nl.dionjansen.recipemanager.repositories.RecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RecipeRepository repository;

    @BeforeEach
    public void initEach() {repository.deleteAll();}

    @Test
    public void shouldCreate() throws Exception {
        Assertions.assertThat(repository.count()).isEqualTo(0);

        RecipeDto dto = new RecipeDto();
        dto.setName("Some Recipe");
        dto.setNoOfServings(4);

        MvcResult result =
                this.mvc.perform(post("/recipes").content(objectMapper.writeValueAsBytes(dto)).contentType("application/json"))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn();

        RecipeDto response = objectMapper.readValue(result.getResponse().getContentAsString(), RecipeDto.class);

        Assertions.assertThat(repository.count()).isEqualTo(1);
        Assertions.assertThat(repository.findById(response.getId()).isPresent()).isTrue();
        Assertions.assertThat(dto.getName()).isEqualTo(response.getName());
        Assertions.assertThat(dto.getNoOfServings()).isEqualTo(response.getNoOfServings());
    }

    @Test
    public void shouldReturnEmptyList() throws Exception {
        MvcResult result =
                this.mvc.perform(get("/recipes"))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn();

        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }

    @Test
    public void shouldReturnNonEmptyList() throws Exception {
        // Check for empty database
        Assertions.assertThat(repository.count()).isEqualTo(0);

        // Create recipe
        Recipe recipe = new Recipe();
        recipe.setName("Some Recipe");
        recipe.setNoOfServings(1);
        repository.saveAndFlush(recipe);

        // Check endpoint
        MvcResult result =
                this.mvc.perform(get("/recipes"))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn();

        List<RecipeDto> recipes = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RecipeDto>>(){});

        Assertions.assertThat(repository.count()).isEqualTo(recipes.size());
        Assertions.assertThat(recipes.stream().anyMatch(r -> r.getName().equals(recipe.getName()))).isTrue();
    }

    @Test
    public void shouldReturnNotFound() throws Exception {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        }
        while (repository.findById(uuid).isPresent());
        // Here the uuid is unique
        this.mvc.perform(get("/recipes/" + uuid.toString()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotUpdateBadRequest() throws Exception {
        RecipeDto dto = new RecipeDto();
        dto.setId(UUID.randomUUID());

        this.mvc.perform(put("/recipes/" + UUID.randomUUID().toString()).content(objectMapper.writeValueAsBytes(dto)).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void shouldUpdateRecipe() throws Exception {
        Integer noOfServings = 1337;

        // Create recipe
        Recipe recipe = new Recipe();
        recipe.setName("Some Recipe");
        recipe.setNoOfServings(1);
        repository.saveAndFlush(recipe);

        // Check endpoint
        MvcResult result =
                this.mvc.perform(get("/recipes/" + recipe.getId()))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn();

        RecipeDto dto = objectMapper.readValue(result.getResponse().getContentAsString(), RecipeDto.class);
        dto.setNoOfServings(noOfServings);

        this.mvc.perform(put("/recipes/" + recipe.getId()).content(objectMapper.writeValueAsBytes(dto)).contentType("application/json"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Optional<Recipe> optional = repository.findById(dto.getId());
        Assertions.assertThat(optional.isPresent()).isTrue();
        Assertions.assertThat(optional.get().getNoOfServings()).isEqualTo(noOfServings);
    }

    @Test
    public void shouldDelete() throws Exception {
        // Create recipe
        Recipe recipe = new Recipe();
        recipe.setName("Some Recipe");
        recipe.setNoOfServings(1);
        repository.saveAndFlush(recipe);

        Assertions.assertThat(repository.findById(recipe.getId()).isPresent()).isTrue();

        this.mvc.perform(delete("/recipes/" + recipe.getId()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Assertions.assertThat(repository.findById(recipe.getId()).isPresent()).isFalse();

    }
}
