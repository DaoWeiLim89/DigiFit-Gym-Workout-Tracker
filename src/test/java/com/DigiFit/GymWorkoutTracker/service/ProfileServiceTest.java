package com.DigiFit.GymWorkoutTracker.service;

import com.DigiFit.GymWorkoutTracker.model.Profile;
import com.DigiFit.GymWorkoutTracker.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService Tests")
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    private UUID userId;
    private Profile profile;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profile = new Profile(userId, "Test User");
        profile.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Should get profile by user ID")
    void testGetProfileByUserId() {
        when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));

        Profile found = profileService.getProfileByUserId(userId);

        assertNotNull(found);
        assertEquals("Test User", found.getName());
        verify(profileRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when profile not found")
    void testGetProfileByUserIdNotFound() {
        when(profileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            profileService.getProfileByUserId(userId);
        });
    }

    @Test
    @DisplayName("Should create new profile")
    void testCreateProfile() {
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile created = profileService.createProfile(profile);

        assertNotNull(created);
        assertNotNull(created.getCreatedAt());
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    @DisplayName("Should update profile")
    void testUpdateProfile() {
        Profile updates = new Profile();
        updates.setName("Updated Name");
        updates.setWeight(80.0);
        updates.setHeight(185.0);

        when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile updated = profileService.updateProfile(userId, updates);

        assertNotNull(updated);
        verify(profileRepository, times(1)).findById(userId);
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    @DisplayName("Should check if profile exists")
    void testProfileExists() {
        when(profileRepository.existsById(userId)).thenReturn(true);

        boolean exists = profileService.profileExists(userId);

        assertTrue(exists);
        verify(profileRepository, times(1)).existsById(userId);
    }

    @Test
    @DisplayName("Should delete profile")
    void testDeleteProfile() {
        when(profileRepository.existsById(userId)).thenReturn(true);
        doNothing().when(profileRepository).deleteById(userId);

        profileService.deleteProfile(userId);

        verify(profileRepository, times(1)).existsById(userId);
        verify(profileRepository, times(1)).deleteById(userId);
    }
}