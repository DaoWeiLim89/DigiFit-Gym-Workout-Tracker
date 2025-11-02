package com.DigiFit.GymWorkoutTracker.repository;

import com.DigiFit.GymWorkoutTracker.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    /**
     * Find all exercises for a specific workout split
     */
    List<Exercise> findBySplitId(Long splitId);

    /**
     * Find all exercises for a specific workout split ordered by orderIndex
     */
    List<Exercise> findBySplitIdOrderByOrderIndex(Long splitId);

    /**
     * Find an exercise by ID that belongs to a specific user (for security)
     */
    @Query("SELECT e FROM Exercise e WHERE e.id = :exerciseId AND e.split.user.id = :userId")
    Optional<Exercise> findByIdAndUserId(@Param("exerciseId") Long exerciseId, @Param("userId") UUID userId);

    /**
     * Find exercises by name for a specific user
     */
    @Query("SELECT e FROM Exercise e WHERE e.split.user.id = :userId AND LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Exercise> findByUserIdAndNameContaining(@Param("userId") UUID userId, @Param("name") String name);
}