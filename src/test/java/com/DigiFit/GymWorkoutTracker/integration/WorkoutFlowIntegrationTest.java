package com.DigiFit.GymWorkoutTracker.integration;

import com.DigiFit.GymWorkoutTracker.model.*;
import com.DigiFit.GymWorkoutTracker.repository.*;
import com.DigiFit.GymWorkoutTracker.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "DATABASE_URL=http://localhost:54321",
        "SERVICE_ROLES=test-key",
        "supabase.jwt.secret=test-secret-key-for-testing-purposes-only-min-256-bits"
})
@DisplayName("Complete Workout Flow Integration Test")
class WorkoutFlowIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private WorkoutSplitRepository splitRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutEntryRepository entryRepository;

    // ⭐ ADD THIS ⭐
    @Autowired
    private EntityManager entityManager;

    @MockBean
    private AuthService authService;

    private Profile testProfile;

    @Test
    @DisplayName("Complete workout tracking flow")
    void testCompleteWorkoutFlow() {
        // Step 1: Create profile (simulates Supabase user)
        UUID userId = UUID.randomUUID();
        testProfile = new Profile(userId, "Test User");
        testProfile.setEmail("test@example.com");
        testProfile = profileRepository.save(testProfile);

        assertNotNull(testProfile.getId());

        // Step 2: Create workout split
        WorkoutSplit legDay = new WorkoutSplit("Leg Day");
        legDay.setUser(testProfile);
        legDay.setActive(true);
        legDay = splitRepository.save(legDay);

        // Step 3: Add exercises
        Exercise deadlift = new Exercise("Deadlift", 3);
        deadlift.setSplit(legDay);
        deadlift.setOrderIndex(0);
        deadlift = exerciseRepository.save(deadlift);

        Exercise squat = new Exercise("Squat", 4);
        squat.setSplit(legDay);
        squat.setOrderIndex(1);
        squat = exerciseRepository.save(squat);

        // Step 4: Log workout entries (Week 1)
        WorkoutEntry deadliftWeek1 = new WorkoutEntry(
                LocalDate.of(2024, 10, 22), 315.0, 5
        );
        deadliftWeek1.setExercise(deadlift);
        entryRepository.save(deadliftWeek1);

        WorkoutEntry squatWeek1 = new WorkoutEntry(
                LocalDate.of(2024, 10, 22), 275.0, 8
        );
        squatWeek1.setExercise(squat);
        entryRepository.save(squatWeek1);

        // Step 5: Log workout entries (Week 2 - Progress!)
        WorkoutEntry deadliftWeek2 = new WorkoutEntry(
                LocalDate.of(2024, 10, 29), 320.0, 5
        );
        deadliftWeek2.setExercise(deadlift);
        entryRepository.save(deadliftWeek2);

        // ⭐ ADD THIS LINE ⭐
        entityManager.flush();
        entityManager.clear();

        // Step 6: Verify data persistence
        Profile foundProfile = profileRepository.findById(userId).orElse(null);
        assertNotNull(foundProfile);

        // Step 7: Verify split
        WorkoutSplit foundSplit = splitRepository.findById(legDay.getId()).orElse(null);
        assertNotNull(foundSplit);
        assertTrue(foundSplit.isActive());

        // Step 8: Verify exercises
        List<Exercise> exercises = exerciseRepository.findBySplitIdOrderByOrderIndex(legDay.getId());
        assertEquals(2, exercises.size());
        assertEquals("Deadlift", exercises.get(0).getName());
        assertEquals("Squat", exercises.get(1).getName());

        // Step 9: Verify entries
        List<WorkoutEntry> deadliftEntries = entryRepository
                .findByExerciseIdOrderByDateDesc(deadlift.getId());
        assertEquals(2, deadliftEntries.size());

        // Step 10: Verify latest entry
        Exercise foundDeadlift = exerciseRepository.findByIdWithEntries(deadlift.getId());        WorkoutEntry latest = foundDeadlift.getLatestEntry();
        assertEquals(320.0, latest.getWeight());
        assertEquals(LocalDate.of(2024, 10, 29), latest.getDate());

        // Step 11: Verify active split retrieval
        Profile profileWithSplits = profileRepository.findById(userId).orElse(null);
        WorkoutSplit activeSplit = profileWithSplits.getActiveSplit();
        assertNotNull(activeSplit);
        assertEquals("Leg Day", activeSplit.getName());
    }

    @Test
    @DisplayName("Should track progress over multiple weeks")
    void testProgressTracking() {
        UUID userId = UUID.randomUUID();
        testProfile = new Profile(userId, "Test User");
        testProfile = profileRepository.save(testProfile);

        WorkoutSplit split = new WorkoutSplit("Leg Day");
        split.setUser(testProfile);
        split = splitRepository.save(split);

        Exercise deadlift = new Exercise("Deadlift", 3);
        deadlift.setSplit(split);
        deadlift = exerciseRepository.save(deadlift);

        // Log 6 weeks of progressive overload
        double[] weights = {295, 300, 305, 310, 315, 320};
        LocalDate startDate = LocalDate.of(2024, 9, 17);

        for (int week = 0; week < 6; week++) {
            WorkoutEntry entry = new WorkoutEntry(
                    startDate.plusWeeks(week),
                    weights[week],
                    5
            );
            entry.setExercise(deadlift);
            entryRepository.save(entry);
        }

        // Verify progress
        List<WorkoutEntry> entries = entryRepository
                .findByExerciseIdOrderByDateDesc(deadlift.getId());

        assertEquals(6, entries.size());

        // Latest should be heaviest
        assertEquals(320.0, entries.get(0).getWeight());
    }

    @Test
    @DisplayName("Should enforce data security by user ID")
    void testDataSecurityByUserId() {
        // Create two users
        UUID user1Id = UUID.randomUUID();
        Profile user1 = new Profile(user1Id, "User 1");
        user1 = profileRepository.save(user1);

        UUID user2Id = UUID.randomUUID();
        Profile user2 = new Profile(user2Id, "User 2");
        user2 = profileRepository.save(user2);

        // User 1 creates a split and exercise
        WorkoutSplit split1 = new WorkoutSplit("User 1 Split");
        split1.setUser(user1);
        split1 = splitRepository.save(split1);

        Exercise exercise1 = new Exercise("Deadlift", 3);
        exercise1.setSplit(split1);
        exercise1 = exerciseRepository.save(exercise1);

        WorkoutEntry entry1 = new WorkoutEntry(LocalDate.now(), 315.0, 5);
        entry1.setExercise(exercise1);
        entry1 = entryRepository.save(entry1);

        // User 2 should NOT be able to access User 1's entry
        WorkoutEntry found = entryRepository
                .findByIdAndUserId(entry1.getId(), user2Id)
                .orElse(null);

        assertNull(found); // Security check passed!
    }
}