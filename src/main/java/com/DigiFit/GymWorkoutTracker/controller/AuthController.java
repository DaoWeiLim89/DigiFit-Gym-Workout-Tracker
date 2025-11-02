package com.DigiFit.GymWorkoutTracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Verify that the user is authenticated
     * This endpoint is called after Supabase authentication to verify the JWT works
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "userId", userId.toString()
        ));
    }

    /**
     * Health check endpoint (public)
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}