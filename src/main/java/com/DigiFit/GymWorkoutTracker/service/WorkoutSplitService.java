package com.DigiFit.GymWorkoutTracker.service;

import com.DigiFit.GymWorkoutTracker.model.Profile;
import com.DigiFit.GymWorkoutTracker.model.WorkoutSplit;
import com.DigiFit.GymWorkoutTracker.repository.ProfileRepository;
import com.DigiFit.GymWorkoutTracker.repository.WorkoutSplitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutSplitService {

    private final WorkoutSplitRepository splitRepository;
    private final ProfileRepository profileRepository;

    public WorkoutSplitService(WorkoutSplitRepository splitRepository, ProfileRepository profileRepository) {
        this.splitRepository = splitRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Get all splits for a user
     */
    public List<WorkoutSplit> getUserSplits(UUID userId) {
        return splitRepository.findByUserId(userId);
    }

    /**
     * Get a specific split if it belongs to the user
     */
    public WorkoutSplit getSplitByIdAndUser(Long splitId, UUID userId) {
        return splitRepository.findByIdAndUserId(splitId, userId)
                .orElseThrow(() -> new RuntimeException("Split not found or access denied"));
    }

    /**
     * Get the active split for a user
     */
    public WorkoutSplit getActiveSplit(UUID userId) {
        return splitRepository.findActiveByUserId(userId).orElse(null);
    }

    /**
     * Create a new split for a user
     */
    @Transactional
    public WorkoutSplit createSplit(UUID userId, WorkoutSplit split) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        split.setUser(profile);
        split.setActive(false); // New splits are inactive by default

        return splitRepository.save(split);
    }

    /**
     * Update an existing split
     */
    @Transactional
    public WorkoutSplit updateSplit(Long splitId, UUID userId, WorkoutSplit updates) {
        WorkoutSplit existing = getSplitByIdAndUser(splitId, userId);

        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }

        return splitRepository.save(existing);
    }

    /**
     * Set a split as active (deactivates others)
     */
    @Transactional
    public WorkoutSplit setActiveSplit(Long splitId, UUID userId) {
        // First, deactivate all splits for this user
        List<WorkoutSplit> userSplits = splitRepository.findByUserId(userId);
        for (WorkoutSplit split : userSplits) {
            split.setActive(false);
        }
        splitRepository.saveAll(userSplits);

        // Then activate the selected split
        WorkoutSplit splitToActivate = getSplitByIdAndUser(splitId, userId);
        splitToActivate.setActive(true);

        return splitRepository.save(splitToActivate);
    }

    /**
     * Delete a split
     */
    @Transactional
    public void deleteSplit(Long splitId, UUID userId) {
        WorkoutSplit split = getSplitByIdAndUser(splitId, userId);
        splitRepository.delete(split);
    }
}