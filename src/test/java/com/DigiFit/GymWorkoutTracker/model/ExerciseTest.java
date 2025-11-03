package com.DigiFit.GymWorkoutTracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exercise Model Tests")
class ExerciseTest {

    private Exercise exercise;
    private WorkoutSplit split;

    @BeforeEach
    void setUp() {
        split = new WorkoutSplit("Leg Day");
        exercise = new Exercise("Deadlift", 3);
        exercise.setSplit(split);
    }

    @Test
    @DisplayName("Should create Exercise with all fields")
    void testExerciseCreation() {
        assertNotNull(exercise);
        assertEquals("Deadlift", exercise.getName());
        assertEquals(3, exercise.getSets());
    }

    @Test
    @DisplayName("Should add workout entries to exercise")
    void testAddEntry() {
        WorkoutEntry entry1 = new WorkoutEntry(
                LocalDate.of(2024, 10, 29),
                315.0, 5
        );

        exercise.addEntry(entry1);

        assertEquals(1, exercise.getEntries().size());
        assertTrue(exercise.getEntries().contains(entry1));
        assertEquals(exercise, entry1.getExercise());
    }

    @Test
    @DisplayName("Should remove workout entries from exercise")
    void testRemoveEntry() {
        WorkoutEntry entry1 = new WorkoutEntry(
                LocalDate.of(2024, 10, 29),
                315.0, 5
        );

        exercise.addEntry(entry1);
        assertEquals(1, exercise.getEntries().size());

        exercise.removeEntry(entry1);
        assertEquals(0, exercise.getEntries().size());
        assertNull(entry1.getExercise());
    }

    @Test
    @DisplayName("Should get latest entry")
    void testGetLatestEntry() {
        WorkoutEntry entry1 = new WorkoutEntry(
                LocalDate.of(2024, 10, 22), 315.0, 5
        );
        WorkoutEntry entry2 = new WorkoutEntry(
                LocalDate.of(2024, 11, 5), 325.0, 5
        );
        WorkoutEntry entry3 = new WorkoutEntry(
                LocalDate.of(2024, 10, 29), 320.0, 5
        );

        exercise.addEntry(entry1);
        exercise.addEntry(entry2);
        exercise.addEntry(entry3);

        WorkoutEntry latest = exercise.getLatestEntry();

        assertNotNull(latest);
        assertEquals(LocalDate.of(2024, 11, 5), latest.getDate());
        assertEquals(325.0, latest.getWeight());
    }

    @Test
    @DisplayName("Should return null for latest entry when no entries exist")
    void testGetLatestEntryWhenEmpty() {
        assertNull(exercise.getLatestEntry());
    }

    @Test
    @DisplayName("Should get latest weight")
    void testGetLatestWeight() {
        exercise.addEntry(new WorkoutEntry(
                LocalDate.of(2024, 10, 22), 315.0, 5
        ));
        exercise.addEntry(new WorkoutEntry(
                LocalDate.of(2024, 10, 29), 320.0, 5
        ));

        Double latestWeight = exercise.getLatestWeight();

        assertNotNull(latestWeight);
        assertEquals(320.0, latestWeight);
    }

    @Test
    @DisplayName("Should get last workout date")
    void testGetLastWorkoutDate() {
        exercise.addEntry(new WorkoutEntry(
                LocalDate.of(2024, 10, 22), 315.0, 5
        ));
        exercise.addEntry(new WorkoutEntry(
                LocalDate.of(2024, 10, 29), 320.0, 5
        ));

        LocalDate lastDate = exercise.getLastWorkoutDate();

        assertNotNull(lastDate);
        assertEquals(LocalDate.of(2024, 10, 29), lastDate);
    }

    @Test
    @DisplayName("Should maintain order index")
    void testOrderIndex() {
        exercise.setOrderIndex(0);
        assertEquals(0, exercise.getOrderIndex());

        exercise.setOrderIndex(5);
        assertEquals(5, exercise.getOrderIndex());
    }
}