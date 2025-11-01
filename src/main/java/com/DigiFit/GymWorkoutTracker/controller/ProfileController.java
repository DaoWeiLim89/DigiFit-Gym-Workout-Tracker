package com.DigiFit.GymWorkoutTracker.controller;

import com.DigiFit.GymWorkoutTracker.model.Profile;
import com.DigiFit.GymWorkoutTracker.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Get the current authenticated user's profile
     * GET /api/profile
     */
    @GetMapping
    public ResponseEntity<Profile> getCurrentProfile(Authentication authentication) {
        try {
            UUID userId = (UUID) authentication.getPrincipal();
            Profile profile = profileService.getProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Create a new profile for the authenticated user
     * POST /api/profile
     * Called after Supabase signup
     */
    @PostMapping
    public ResponseEntity<Profile> createProfile(
            @RequestBody Profile profile,
            Authentication authentication) {
        try {
            UUID userId = (UUID) authentication.getPrincipal();
            profile.setId(userId);
            Profile createdProfile = profileService.createProfile(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update the current authenticated user's profile
     * PUT /api/profile
     */
    @PutMapping
    public ResponseEntity<Profile> updateProfile(
            @RequestBody Profile profileUpdate,
            Authentication authentication) {
        try {
            UUID userId = (UUID) authentication.getPrincipal();
            Profile updatedProfile = profileService.updateProfile(userId, profileUpdate);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Delete the current authenticated user's profile
     * DELETE /api/profile
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(Authentication authentication) {
        try {
            UUID userId = (UUID) authentication.getPrincipal();
            profileService.deleteProfile(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Check if the current user has a profile
     * GET /api/profile/exists
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> profileExists(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        boolean exists = profileService.profileExists(userId);
        return ResponseEntity.ok(exists);
    }
}