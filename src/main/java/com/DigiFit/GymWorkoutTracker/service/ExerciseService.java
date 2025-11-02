package com.DigiFit.GymWorkoutTracker.service;

import com.DigiFit.GymWorkoutTracker.model.Exercise;
import com.DigiFit.GymWorkoutTracker.model.WorkoutSplit;
import com.DigiFit.GymWorkoutTracker.repository.ExerciseRepository;
import com.DigiFit.GymWorkoutTracker.repository.WorkoutSplitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutSplitRepository splitRepository;

    public ExerciseService(ExerciseRepository exerciseRepository, WorkoutSplitRepository splitRepository) {
        this.exerciseRepository = exerciseRepository;
        this.splitRepository = splitRepository;
    }

    /**
     * Get all exercises for a split (with security check)
     */
    public List<Exercise> getExercisesForSplit(Long splitId, UUID userId) {
        // Verify the split belongs to the user
        WorkoutSplit split = splitRepository.findByIdAndUserId(splitId, userId)
                .orElseThrow(() -> new RuntimeException("Split not found or access denied"));

        return exerciseRepository.findBySplitIdOrderByOrderIndex(splitId);
    }

    /**
     * Get a specific exercise (with security check)
     */
    public Exercise getExerciseByIdAndUser(Long exerciseId, UUID userId) {
        return exerciseRepository.findByIdAndUserId(exerciseId, userId)
                .orElseThrow(() -> new RuntimeException("Exercise not found or access denied"));
    }

    /**
     * Search exercises by name for a user
     */
    public List<Exercise> searchExercisesByName(UUID userId, String name) {
        return exerciseRepository.findByUserIdAndNameContaining(userId, name);
    }

    /**
     * Add an exercise to a split
     */
    @Transactional
    public Exercise addExerciseToSplit(Long splitId, UUID userId, Exercise exercise) {
        // Verify the split belongs to the user
        WorkoutSplit split = splitRepository.findByIdAndUserId(splitId, userId)
                .orElseThrow(() -> new RuntimeException("Split not found or access denied"));

        // Set the order index based on existing exercises
        List<Exercise> existingExercises = exerciseRepository.findBySplitId(splitId);
        exercise.setOrderIndex(existingExercises.size());

        exercise.setSplit(split);
        return exerciseRepository.save(exercise);
    }

    /**
     * Update an exercise
     */
    @Transactional
    public Exercise updateExercise(Long exerciseId, UUID userId, Exercise updates) {
        Exercise existing = getExerciseByIdAndUser(exerciseId, userId);

        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getSets() > 0) {
            existing.setSets(updates.getSets());
        }

        return exerciseRepository.save(existing);
    }

    /**
     * Reorder exercises in a split
     */
    @Transactional
    public List<Exercise> reorderExercises(Long splitId, UUID userId, List<Long> exerciseIds) {
        // Verify the split belongs to the user
        WorkoutSplit split = splitRepository.findByIdAndUserId(splitId, userId)
                .orElseThrow(() -> new RuntimeException("Split not found or access denied"));

        List<Exercise> exercises = exerciseRepository.findBySplitId(splitId);

        // Update order based on the provided list
        for (int i = 0; i < exerciseIds.size(); i++) {
            Long exerciseId = exerciseIds.get(i);
            Exercise exercise = exercises.stream()
                    .filter(e -> e.getId().equals(exerciseId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Invalid exercise ID in reorder list"));

            exercise.setOrderIndex(i);
        }

        return exerciseRepository.saveAll(exercises);
    }

    /**
     * Delete an exercise
     */
    @Transactional
    public void deleteExercise(Long exerciseId, UUID userId) {
        Exercise exercise = getExerciseByIdAndUser(exerciseId, userId);

        // Reorder remaining exercises in the split
        Long splitId = exercise.getSplit().getId();
        List<Exercise> remainingExercises = exerciseRepository.findBySplitIdOrderByOrderIndex(splitId);
        remainingExercises.remove(exercise);

        for (int i = 0; i < remainingExercises.size(); i++) {
            remainingExercises.get(i).setOrderIndex(i);
        }

        exerciseRepository.saveAll(remainingExercises);
        exerciseRepository.delete(exercise);
    }
}