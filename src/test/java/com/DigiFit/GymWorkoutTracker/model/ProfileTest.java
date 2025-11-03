package com.DigiFit.GymWorkoutTracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Profile Model Tests")
class ProfileTest {

    private Profile profile;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profile = new Profile(userId, "Test User");
    }

    @Test
    @DisplayName("Should create Profile with UUID and name")
    void testProfileCreation() {
        assertNotNull(profile);
        assertEquals(userId, profile.getId());
        assertEquals("Test User", profile.getName());
    }

    @Test
    @DisplayName("Should set and get email")
    void testEmail() {
        profile.setEmail("test@example.com");
        assertEquals("test@example.com", profile.getEmail());
    }

    @Test
    @DisplayName("Should set and get physical attributes")
    void testPhysicalAttributes() {
        profile.setWeight(75.5);
        profile.setHeight(180.0);
        profile.setWeightUnit("kg");
        profile.setHeightUnit("cm");
        profile.setGender("male");

        assertEquals(75.5, profile.getWeight());
        assertEquals(180.0, profile.getHeight());
        assertEquals("kg", profile.getWeightUnit());
        assertEquals("cm", profile.getHeightUnit());
        assertEquals("male", profile.getGender());
    }

    @Test
    @DisplayName("Should add workout splits to profile")
    void testAddSplit() {
        WorkoutSplit split = new WorkoutSplit("Leg Day");
        profile.addSplit(split);

        assertEquals(1, profile.getSplits().size());
        assertTrue(profile.getSplits().contains(split));
        assertEquals(profile, split.getUser());
    }

    @Test
    @DisplayName("Should remove workout splits from profile")
    void testRemoveSplit() {
        WorkoutSplit split = new WorkoutSplit("Leg Day");
        profile.addSplit(split);

        profile.removeSplit(split);

        assertEquals(0, profile.getSplits().size());
        assertNull(split.getUser());
    }

    @Test
    @DisplayName("Should get active split")
    void testGetActiveSplit() {
        WorkoutSplit split1 = new WorkoutSplit("PPL");
        split1.setActive(true);

        WorkoutSplit split2 = new WorkoutSplit("Upper Lower");
        split2.setActive(false);

        profile.addSplit(split1);
        profile.addSplit(split2);

        WorkoutSplit active = profile.getActiveSplit();

        assertNotNull(active);
        assertEquals("PPL", active.getName());
    }

    @Test
    @DisplayName("Should return null when no active split")
    void testGetActiveSplitWhenNone() {
        WorkoutSplit split = new WorkoutSplit("PPL");
        split.setActive(false);
        profile.addSplit(split);

        assertNull(profile.getActiveSplit());
    }
}