package com.DigiFit.GymWorkoutTracker;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int orderIndex;  // Order in the split

    @Column(nullable = false)
    private String name; // e.g. "Deadlift"

    private int sets;    // number of sets the user usually does

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "split_id")
    @JsonBackReference // prevent infinite recursion with WorkoutSplit
    private WorkoutSplit split;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // serialize the entries list
    private List<WorkoutEntry> entries = new  ArrayList<>();//prevents null exceptions


    // ----- Constructors -----
    public Exercise() {}
    public Exercise(String name, int sets) {
        this.name = name;
        this.sets = sets;
    }

    // ----- Getters & Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public WorkoutSplit getSplit() { return split; }
    public void setSplit(WorkoutSplit split) { this.split = split; }

    public List<WorkoutEntry> getEntries() { return entries; }
    public void setEntries(List<WorkoutEntry> entries) { this.entries = entries; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    // ----- Helper Methods -----
    public void addEntry(WorkoutEntry entry) {
        entries.add(entry);
        entry.setExercise(this);
    }

    public void removeEntry(WorkoutEntry entry) {
        entries.remove(entry);
        entry.setExercise(null);
    }

    public WorkoutEntry getLatestEntry() {
        return entries.stream()
                .max(Comparator.comparing(WorkoutEntry::getDate))
                .orElse(null);
    }

    // Get latest weight used
    public Double getLatestWeight() {
        WorkoutEntry latest = getLatestEntry();
        return latest != null ? latest.getWeight() : null;
    }

    // Get date of last workout
    public LocalDate getLastWorkoutDate() {
        WorkoutEntry latest = getLatestEntry();
        return latest != null ? latest.getDate() : null;
    }
}