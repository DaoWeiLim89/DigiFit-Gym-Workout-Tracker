package com.DigiFit.GymWorkoutTracker.controller;

import com.DigiFit.GymWorkoutTracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @Autowired
    private AuthService authService;

    /**
     * Handles the signup process by getting information and calling authService.signUp
     * to create account using Supabase
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String name = body.getOrDefault("name", "");
        return ResponseEntity.ok(authService.signUp(email, password, name));
    }

    /**
     * Handles the login process by getting information and calling authService.login
     * to create account using Supabase
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        return ResponseEntity.ok(authService.login(email, password));
    }

    /**
     * Health check endpoint (public)
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}