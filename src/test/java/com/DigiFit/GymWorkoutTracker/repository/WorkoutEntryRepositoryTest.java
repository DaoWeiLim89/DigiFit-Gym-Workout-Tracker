package com.DigiFit.GymWorkoutTracker.repository;

import com.DigiFit.GymWorkoutTracker.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("WorkoutEntry Repository Tests")
class WorkoutEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkoutEntryRepository workoutEntryRepository;

    private Profile profile;
    private WorkoutSplit split;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        profile = new Profile(UUID.randomUUID(), "Test User");
        profile.setEmail("test@example.com");
        entityManager.persist(profile);

        split = new WorkoutSplit("Leg Day");
        split.setUser(profile);
        entityManager.persist(split);

        exercise = new Exercise("Deadlift", 3);
        exercise.setSplit(split);
        entityManager.persist(exercise);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should save and retrieve workout entry")
    void testSaveAndFindWorkoutEntry() {
        WorkoutEntry entry = new WorkoutEntry(LocalDate.now(), 315.0, 5);
        entry.setExercise(exercise);

        WorkoutEntry saved = workoutEntryRepository.save(entry);

        assertNotNull(saved.getId());

        WorkoutEntry found = workoutEntryRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(315.0, found.getWeight());
        assertEquals(5, found.getReps());
    }

    @Test
    @DisplayName("Should find entries by exercise ordered by date")
    void testFindByExerciseIdOrderByDateDesc() {
        WorkoutEntry entry1 = new WorkoutEntry(LocalDate.of(2024, 10, 22), 315.0, 5);
        entry1.setExercise(exercise);

        WorkoutEntry entry2 = new WorkoutEntry(LocalDate.of(2024, 10, 29), 320.0, 5);
        entry2.setExercise(exercise);

        workoutEntryRepository.save(entry1);
        workoutEntryRepository.save(entry2);

        List<WorkoutEntry> entries = workoutEntryRepository
                .findByExerciseIdOrderByDateDesc(exercise.getId());

        assertEquals(2, entries.size());
        // Should be newest first
        assertEquals(LocalDate.of(2024, 10, 29), entries.get(0).getDate());
    }

    @Test
    @DisplayName("Should find entries by date range")
    void testFindByExerciseIdAndDateBetween() {
        WorkoutEntry entry1 = new WorkoutEntry(LocalDate.of(2024, 10, 15), 310.0, 5);
        entry1.setExercise(exercise);
        workoutEntryRepository.save(entry1);

        WorkoutEntry entry2 = new WorkoutEntry(LocalDate.of(2024, 10, 22), 315.0, 5);
        entry2.setExercise(exercise);
        workoutEntryRepository.save(entry2);

        WorkoutEntry entry3 = new WorkoutEntry(LocalDate.of(2024, 10, 29), 320.0, 5);
        entry3.setExercise(exercise);
        workoutEntryRepository.save(entry3);

        List<WorkoutEntry> entries = workoutEntryRepository
                .findByExerciseIdAndDateBetween(
                        exercise.getId(),
                        LocalDate.of(2024, 10, 20),
                        LocalDate.of(2024, 10, 30)
                );

        assertEquals(2, entries.size());
    }

    @Test
    @DisplayName("Should find entry by ID and user ID (security)")
    void testFindByIdAndUserId() {
        WorkoutEntry entry = new WorkoutEntry(LocalDate.now(), 315.0, 5);
        entry.setExercise(exercise);
        WorkoutEntry saved = workoutEntryRepository.save(entry);

        WorkoutEntry found = workoutEntryRepository
                .findByIdAndUserId(saved.getId(), profile.getId())
                .orElse(null);

        assertNotNull(found);
        assertEquals(315.0, found.getWeight());
    }

    @Test
    @DisplayName("Should not find entry for different user")
    void testFindByIdAndUserIdSecurityCheck() {
        WorkoutEntry entry = new WorkoutEntry(LocalDate.now(), 315.0, 5);
        entry.setExercise(exercise);
        WorkoutEntry saved = workoutEntryRepository.save(entry);

        UUID differentUserId = UUID.randomUUID();

        WorkoutEntry found = workoutEntryRepository
                .findByIdAndUserId(saved.getId(), differentUserId)
                .orElse(null);

        assertNull(found); // Security: can't access other user's data
    }
}