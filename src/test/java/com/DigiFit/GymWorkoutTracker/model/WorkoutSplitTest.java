package com.DigiFit.GymWorkoutTracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WorkoutSplit Model Tests")
class WorkoutSplitTest {

    private WorkoutSplit split;
    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = new Profile(UUID.randomUUID(), "Test User");
        split = new WorkoutSplit("Leg Day");
        split.setUser(profile);
    }

    @Test
    @DisplayName("DEBUG: Check what's happening")
    void debugTest() {
        WorkoutSplit split = new WorkoutSplit("Test");
        Profile profile = new Profile(UUID.randomUUID(), "Test");
        split.setUser(profile);

        Exercise ex1 = new Exercise("Squat", 4);
        Exercise ex2 = new Exercise("Deadlift", 3);

        split.addExercise(ex1);
        split.addExercise(ex2);

        System.out.println("ex1 orderIndex: " + ex1.getOrderIndex());
        System.out.println("ex2 orderIndex: " + ex2.getOrderIndex());
        System.out.println("ex1 split: " + (ex1.getSplit() != null ? "set" : "null"));
        System.out.println("List size: " + split.getExercises().size());

        List<Exercise> ordered = split.getExercisesInOrder();
        System.out.println("Ordered list size: " + ordered.size());
        for (Exercise e : ordered) {
            System.out.println("  - " + e.getName() + " (order: " + e.getOrderIndex() + ")");
        }
    }

    @Test
    @DisplayName("Should create WorkoutSplit with name")
    void testSplitCreation() {
        assertNotNull(split);
        assertEquals("Leg Day", split.getName());
        assertFalse(split.isActive()); // Default is false
    }

    @Test
    @DisplayName("Should add exercises to split")
    void testAddExercise() {
        Exercise ex1 = new Exercise("Deadlift", 3);
        Exercise ex2 = new Exercise("Squat", 4);

        split.addExercise(ex1);
        split.addExercise(ex2);

        assertEquals(2, split.getExercises().size());
        assertEquals(0, ex1.getOrderIndex()); // Auto-assigned
        assertEquals(1, ex2.getOrderIndex()); // Auto-assigned
    }

    @Test
    @DisplayName("Should remove exercises from split")
    void testRemoveExercise() {
        Exercise ex = new Exercise("Deadlift", 3);
        split.addExercise(ex);

        split.removeExercise(ex);

        assertEquals(0, split.getExercises().size());
        assertNull(ex.getSplit());
    }

    @Test
    @DisplayName("Should get exercises in order")
    void testGetExercisesInOrder() {
        Exercise ex1 = new Exercise("Squat", 4);
        ex1.setOrderIndex(2);

        Exercise ex2 = new Exercise("Deadlift", 3);
        ex2.setOrderIndex(0);

        Exercise ex3 = new Exercise("Leg Press", 3);
        ex3.setOrderIndex(1);

        split.getExercises().add(ex1);
        split.getExercises().add(ex2);
        split.getExercises().add(ex3);

        List<Exercise> ordered = split.getExercisesInOrder();

        assertEquals("Deadlift", ordered.get(0).getName());
        assertEquals("Leg Press", ordered.get(1).getName());
        assertEquals("Squat", ordered.get(2).getName());
    }

    @Test
    @DisplayName("Should get exercise count")
    void testGetExerciseCount() {
        split.addExercise(new Exercise("Deadlift", 3));
        split.addExercise(new Exercise("Squat", 4));
        split.addExercise(new Exercise("Leg Press", 3));

        assertEquals(3, split.getExerciseCount());
    }

    @Test
    @DisplayName("Should set and get active status")
    void testActiveStatus() {
        assertFalse(split.isActive());

        split.setActive(true);
        assertTrue(split.isActive());
    }
}