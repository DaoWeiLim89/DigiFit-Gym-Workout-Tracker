package com.DigiFit.GymWorkoutTracker.controller;

import com.DigiFit.GymWorkoutTracker.model.Exercise;
import com.DigiFit.GymWorkoutTracker.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    /**
     * Get all exercises for a specific split
     */
    @GetMapping("/split/{splitId}")
    public ResponseEntity<List<Exercise>> getSplitExercises(
            @PathVariable Long splitId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            List<Exercise> exercises = exerciseService.getExercisesForSplit(splitId, userId);
            return ResponseEntity.ok(exercises);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get a specific exercise
     */
    @GetMapping("/{exerciseId}")
    public ResponseEntity<Exercise> getExercise(
            @PathVariable Long exerciseId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            Exercise exercise = exerciseService.getExerciseByIdAndUser(exerciseId, userId);
            return ResponseEntity.ok(exercise);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search exercises by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Exercise>> searchExercises(
            @RequestParam String name,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<Exercise> exercises = exerciseService.searchExercisesByName(userId, name);
        return ResponseEntity.ok(exercises);
    }

    /**
     * Add an exercise to a split
     */
    @PostMapping("/split/{splitId}")
    public ResponseEntity<Exercise> addExerciseToSplit(
            @PathVariable Long splitId,
            @RequestBody Exercise exercise,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            Exercise created = exerciseService.addExerciseToSplit(splitId, userId, exercise);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an exercise
     */
    @PutMapping("/{exerciseId}")
    public ResponseEntity<Exercise> updateExercise(
            @PathVariable Long exerciseId,
            @RequestBody Exercise exerciseUpdate,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            Exercise updated = exerciseService.updateExercise(exerciseId, userId, exerciseUpdate);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reorder exercises in a split
     */
    @PutMapping("/split/{splitId}/reorder")
    public ResponseEntity<List<Exercise>> reorderExercises(
            @PathVariable Long splitId,
            @RequestBody List<Long> exerciseIds,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            List<Exercise> reordered = exerciseService.reorderExercises(splitId, userId, exerciseIds);
            return ResponseEntity.ok(reordered);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete an exercise
     */
    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(
            @PathVariable Long exerciseId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            exerciseService.deleteExercise(exerciseId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}