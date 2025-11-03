package com.DigiFit.GymWorkoutTracker.service;

import com.DigiFit.GymWorkoutTracker.model.Profile;
import com.DigiFit.GymWorkoutTracker.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AuthService {

    @Value("${DATABASE_URL}")
    private String supabaseUrl;

    @Value("${SERVICE_ROLES}")
    private String supabaseServiceKey;

    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Signup user using Supabase Auth API
     */
    public Map<String, Object> signUp(String email, String password, String name) {
        String url = supabaseUrl + "/auth/v1/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseServiceKey);
        headers.set("Authorization", "Bearer " + supabaseServiceKey);

        Map<String, String> body = Map.of(
                "email", email,
                "password", password
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Supabase signup failed: " + response.getStatusCode());
        }

        Map<String, Object> userData = (Map<String, Object>) response.getBody().get("user");
        String userId = (String) userData.get("id");

        // Create local profile record if not exists
        profileRepository.findById(UUID.fromString(userId))
                .orElseGet(() -> {
                    Profile profile = new Profile();
                    profile.setId(UUID.fromString(userId));
                    profile.setEmail(email);
                    profile.setName(name);
                    return profileRepository.save(profile);
                });

        return Map.of(
                "user", userData
        );
    }

    /**
     * Login user using Supabase Auth API
     */
    public Map<String, Object> login(String email, String password) {
        String url = supabaseUrl + "/auth/v1/token?grant_type=password";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseServiceKey);

        Map<String, String> body = Map.of(
                "email", email,
                "password", password
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Supabase login failed: " + response.getStatusCode());
        }

        Map<String, Object> responseBody = response.getBody();

        // Contains Supabase's JWT token and user info
        return Map.of(
                "access_token", responseBody.get("access_token"),
                "refresh_token", responseBody.get("refresh_token"),
                "user", responseBody.get("user")
        );
    }
}