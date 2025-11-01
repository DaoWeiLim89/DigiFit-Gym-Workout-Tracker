package com.DigiFit.GymWorkoutTracker;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.ArrayList;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkoutSplit> splits = new ArrayList<>();

    // ----- Constructors -----
    public User() {}

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // ----- Getters & Setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
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