package com.DigiFit.GymWorkoutTracker;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;


@Entity
public class WorkoutSplit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Leg Day", "Push Day"
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // foreign key column
    @JsonBackReference // prevents infinite recursion when serializing
    private User user;

    @OneToMany(mappedBy = "split", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // serialize the list of exercises
    private List<Exercise> exercises = new ArrayList<>();

    // ----- Constructors -----
    public WorkoutSplit() {}
    public WorkoutSplit(String name) {
        this.name = name;
        this.active = false;
    }

    // ----- Getters & Setters -----

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    // ----- Helper Methods -----
    public void addExercise(Exercise exercise) {
        exercise.setOrderIndex(exercises.size()); // Auto-assign order
        exercises.add(exercise);
        exercise.setSplit(this);
    }

    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
        exercise.setSplit(null);
    }

    // Get exercises in order
    public List<Exercise> getExercisesInOrder() {
        return exercises.stream()
                .sorted(Comparator.comparingInt(Exercise::getOrderIndex))
                .toList();
    }

    // Get exercise count (useful for API responses)
    public int getExerciseCount() {
        return exercises.size();
    }
}