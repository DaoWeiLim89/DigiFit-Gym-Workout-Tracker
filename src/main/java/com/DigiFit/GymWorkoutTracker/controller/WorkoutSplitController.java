package com.DigiFit.GymWorkoutTracker.controller;

import com.DigiFit.GymWorkoutTracker.model.WorkoutSplit;
import com.DigiFit.GymWorkoutTracker.service.WorkoutSplitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/splits")
public class WorkoutSplitController {

    private final WorkoutSplitService splitService;

    public WorkoutSplitController(WorkoutSplitService splitService) {
        this.splitService = splitService;
    }

    /**
     * Get all workout splits for the authenticated user
     */
    @GetMapping
    public ResponseEntity<List<WorkoutSplit>> getUserSplits(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<WorkoutSplit> splits = splitService.getUserSplits(userId);
        return ResponseEntity.ok(splits);
    }

    /**
     * Get a specific workout split by ID
     */
    @GetMapping("/{splitId}")
    public ResponseEntity<WorkoutSplit> getSplit(
            @PathVariable Long splitId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            WorkoutSplit split = splitService.getSplitByIdAndUser(splitId, userId);
            return ResponseEntity.ok(split);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get the active workout split
     */
    @GetMapping("/active")
    public ResponseEntity<WorkoutSplit> getActiveSplit(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        WorkoutSplit activeSplit = splitService.getActiveSplit(userId);
        if (activeSplit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(activeSplit);
    }

    /**
     * Create a new workout split
     */
    @PostMapping
    public ResponseEntity<WorkoutSplit> createSplit(
            @RequestBody WorkoutSplit split,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        WorkoutSplit created = splitService.createSplit(userId, split);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update a workout split
     */
    @PutMapping("/{splitId}")
    public ResponseEntity<WorkoutSplit> updateSplit(
            @PathVariable Long splitId,
            @RequestBody WorkoutSplit splitUpdate,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            WorkoutSplit updated = splitService.updateSplit(splitId, userId, splitUpdate);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Set a split as active
     */
    @PutMapping("/{splitId}/activate")
    public ResponseEntity<WorkoutSplit> activateSplit(
            @PathVariable Long splitId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            WorkoutSplit activated = splitService.setActiveSplit(splitId, userId);
            return ResponseEntity.ok(activated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a workout split
     */
    @DeleteMapping("/{splitId}")
    public ResponseEntity<Void> deleteSplit(
            @PathVariable Long splitId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            splitService.deleteSplit(splitId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}