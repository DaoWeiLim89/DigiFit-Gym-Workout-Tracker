package com.DigiFit.GymWorkoutTracker.repository;

import com.DigiFit.GymWorkoutTracker.model.WorkoutEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutEntryRepository extends JpaRepository<WorkoutEntry, Long> {

    /**
     * Find all entries for a specific exercise
     */
    List<WorkoutEntry> findByExerciseIdOrderByDateDesc(Long exerciseId);

    /**
     * Find entries for an exercise within a date range
     */
    @Query("SELECT we FROM WorkoutEntry we WHERE we.exercise.id = :exerciseId " +
            "AND we.date BETWEEN :startDate AND :endDate ORDER BY we.date")
    List<WorkoutEntry> findByExerciseIdAndDateBetween(
            @Param("exerciseId") Long exerciseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Find an entry by ID that belongs to a specific user (for security)
     */
    @Query("SELECT we FROM WorkoutEntry we WHERE we.id = :entryId " +
            "AND we.exercise.split.user.id = :userId")
    Optional<WorkoutEntry> findByIdAndUserId(
            @Param("entryId") Long entryId,
            @Param("userId") UUID userId
    );

    /**
     * Find all entries for a user
     */
    @Query("SELECT we FROM WorkoutEntry we WHERE we.exercise.split.user.id = :userId " +
            "ORDER BY we.date DESC")
    List<WorkoutEntry> findByUserId(@Param("userId") UUID userId);
}