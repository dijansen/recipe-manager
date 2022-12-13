package nl.dionjansen.recipemanager.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private Boolean vegetarian;

    private Integer noOfServings;

    @ElementCollection
    @CollectionTable(name = "ingredients", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "ingredients")
    private List<String> ingredients;

    @ElementCollection
    @CollectionTable(name = "instructions", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "instructions")
    private List<String> instructions;

}
