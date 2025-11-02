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

    @Autowired
    private AuthService authService;

    /**
     * Handles the signup process by getting information and calling authService.signUp
     * to create account using Supabase
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String name = body.getOrDefault("name", "");
        return ResponseEntity.ok(AuthService.signUp(email, password, name));
    }

    /**
     * Handles the login process by getting information and calling authService.login
     * to create account using Supabase
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        return ResponseEntity.ok(AuthService.login(email, password));
    }

    /**
     * Health check endpoint (public)
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}