package com.DigiFit.GymWorkoutTracker.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.time.OffsetDateTime;

@Entity
@Table(name = "profiles")
public class Profile {

    // ----- Primary Key -----
    // Supabase uses UUIDs (from auth.users)
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    // ----- Basic Information -----
    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private Double weight;

    @Column(nullable = true)
    private Double height;

    @Column(name = "weight_unit", nullable = false)
    private String weightUnit = "kg";

    @Column(name = "height_unit", nullable = false)
    private String heightUnit = "cm";

    @Column(nullable = true)
    private String gender;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // ----- Relationships -----
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkoutSplit> splits = new ArrayList<>();

    // ----- Constructors -----
    public Profile() {}

    public Profile(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    // ----- Getters & Setters -----
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public String getWeightUnit() { return weightUnit; }
    public void setWeightUnit(String weightUnit) { this.weightUnit = weightUnit; }

    public String getHeightUnit() { return heightUnit; }
    public void setHeightUnit(String heightUnit) { this.heightUnit = heightUnit; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public List<WorkoutSplit> getSplits() { return splits; }
    public void setSplits(List<WorkoutSplit> splits) { this.splits = splits; }

    // ----- Helper Methods -----
    public void addSplit(WorkoutSplit split) {
        splits.add(split);
        split.setUser(this);
    }

    public void removeSplit(WorkoutSplit split) {
        splits.remove(split);
        split.setUser(null);
    }

    public WorkoutSplit getActiveSplit() {
        return splits.stream()
                .filter(WorkoutSplit::isActive)
                .findFirst()
                .orElse(null);
    }
}