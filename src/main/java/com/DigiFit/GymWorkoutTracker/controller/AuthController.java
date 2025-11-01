package com.DigiFit.GymWorkoutTracker.controller;

import com.DigiFit.GymWorkoutTracker.model.User;
import com.DigiFit.GymWorkoutTracker.security.JwtService;
import com.DigiFit.GymWorkoutTracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // ✅ Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Save new user to database (Supabase)
        userService.saveUser(user);
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }

    // ✅ Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Validate username/password
        if (!userService.validateUser(username, password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        // Generate JWT for authenticated user
        String token = jwtService.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
