package com.DigiFit.GymWorkoutTracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WorkoutEntry Model Tests")
class WorkoutEntryTest {

    private WorkoutEntry workoutEntry;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        exercise = new Exercise("Deadlift", 3);
        workoutEntry = new WorkoutEntry(
                LocalDate.of(2024, 11, 5),
                315.0,
                5
        );
    }

    @Test
    @DisplayName("Should create WorkoutEntry with all fields")
    void testWorkoutEntryCreation() {
        assertNotNull(workoutEntry);
        assertEquals(LocalDate.of(2024, 11, 5), workoutEntry.getDate());
        assertEquals(315.0, workoutEntry.getWeight());
        assertEquals(5, workoutEntry.getReps());
    }

    @Test
    @DisplayName("Should establish relationship with Exercise")
    void testExerciseRelationship() {
        workoutEntry.setExercise(exercise);

        assertNotNull(workoutEntry.getExercise());
        assertEquals("Deadlift", workoutEntry.getExercise().getName());
    }

    @Test
    @DisplayName("Should handle decimal weights correctly")
    void testDecimalWeights() {
        WorkoutEntry entry = new WorkoutEntry(LocalDate.now(), 225.5, 8);
        assertEquals(225.5, entry.getWeight());
    }

    @Test
    @DisplayName("Should update weight and reps")
    void testUpdateValues() {
        workoutEntry.setWeight(320.0);
        workoutEntry.setReps(6);

        assertEquals(320.0, workoutEntry.getWeight());
        assertEquals(6, workoutEntry.getReps());
    }
}