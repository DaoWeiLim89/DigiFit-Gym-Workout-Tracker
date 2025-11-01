package com.DigiFit.GymWorkoutTracker.service;

import com.DigiFit.GymWorkoutTracker.model.Profile;
import com.DigiFit.GymWorkoutTracker.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getProfileByUserId(UUID userId) {
        return profileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Profile createProfile(Profile profile) {
        profile.setCreatedAt(OffsetDateTime.now());
        return profileRepository.save(profile);
    }

    public Profile updateProfile(UUID userId, Profile updates) {
        Profile existing = getProfileByUserId(userId);
        
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getWeight() != null) existing.setWeight(updates.getWeight());
        if (updates.getHeight() != null) existing.setHeight(updates.getHeight());
        if (updates.getWeightUnit() != null) existing.setWeightUnit(updates.getWeightUnit());
        if (updates.getHeightUnit() != null) existing.setHeightUnit(updates.getHeightUnit());
        if (updates.getGender() != null) existing.setGender(updates.getGender());
        
        return profileRepository.save(existing);
    }
    public void deleteProfile(UUID userId) {
        if (!profileRepository.existsById(userId)) {
            throw new RuntimeException("Profile not found");
        }
        profileRepository.deleteById(userId);
    }

    public boolean profileExists(UUID userId) {
        return profileRepository.existsById(userId);
    }
}
