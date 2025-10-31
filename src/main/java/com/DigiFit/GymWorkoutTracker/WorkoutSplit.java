package com.digifit.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.List;

@Entity
public class WorkoutSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. "Leg Day", "Push Day"

    @ManyToOne
    @JoinColumn(name = "user_id") // foreign key column
    @JsonBackReference // prevents infinite recursion when serializing
    private User user;

    @OneToMany(mappedBy = "split", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // serialize the list of exercises
    private List<Exercise> exercises;

    // ----- Constructors -----
    public WorkoutSplit() {}
    public WorkoutSplit(String name) {
        this.name = name;
    }

    // ----- Getters & Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    // ----- Helper Methods -----
    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
        exercise.setSplit(this);
    }

    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
        exercise.setSplit(null);
    }
}