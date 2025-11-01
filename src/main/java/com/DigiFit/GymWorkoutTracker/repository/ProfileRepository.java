package com.DigiFit.GymWorkoutTracker.repository;

import com.DigiFit.GymWorkoutTracker.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}
