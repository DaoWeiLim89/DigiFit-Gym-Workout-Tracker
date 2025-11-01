package com.DigiFit.GymWorkoutTracker.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;

@Entity
@Table(name = "workout_entries")
public class WorkoutEntry {

    // ----- Primary Key -----
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- Basic Info -----
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private int reps;

    // ----- Relationships -----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    @JsonBackReference // prevent recursion with Exercise
    private Exercise exercise;

    // ----- Constructors -----
    public WorkoutEntry() {}

    public WorkoutEntry(LocalDate date, double weight, int reps) {
        this.date = date;
        this.weight = weight;
        this.reps = reps;
    }

    // ----- Getters & Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }
}