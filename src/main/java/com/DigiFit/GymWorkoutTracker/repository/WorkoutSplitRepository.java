package com.DigiFit.GymWorkoutTracker.repository;

import com.DigiFit.GymWorkoutTracker.model.WorkoutSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutSplitRepository extends JpaRepository<WorkoutSplit, Long> {

    /**
     * Find all workout splits for a specific user
     */
    @Query("SELECT ws FROM WorkoutSplit ws WHERE ws.user.id = :userId")
    List<WorkoutSplit> findByUserId(@Param("userId") UUID userId);

    /**
     * Find active split for a user
     */
    @Query("SELECT ws FROM WorkoutSplit ws WHERE ws.user.id = :userId AND ws.active = true")
    Optional<WorkoutSplit> findActiveByUserId(@Param("userId") UUID userId);

    /**
     * Find a specific split by ID and user ID (for security)
     */
    @Query("SELECT ws FROM WorkoutSplit ws WHERE ws.id = :splitId AND ws.user.id = :userId")
    Optional<WorkoutSplit> findByIdAndUserId(@Param("splitId") Long splitId, @Param("userId") UUID userId);
}