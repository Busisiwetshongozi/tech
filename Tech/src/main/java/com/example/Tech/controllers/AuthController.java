package com.example.Tech.controllers;

import com.example.Tech.entities.User;
import com.example.Tech.services.AuthService;
import com.example.Tech.services.UserService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * Register a new user with Firebase UID and additional data (name, phone, etc.)
     * @param authHeader Authorization token from Firebase
     * @param userData Map containing user information (name, phone, address, etc.)
     * @return ResponseEntity containing user registration status
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> userData
    ) {
        // Check if the Authorization header is present and valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        // Extract token from the Authorization header
        String token = authHeader.substring(7);

        try {
            // Verify the token using Firebase Authentication
            FirebaseToken decodedToken = authService.verifyToken(token);
            String firebaseUid = decodedToken.getUid();  // Firebase UID
            String email = decodedToken.getEmail(); // Firebase email

            // Extract additional user data from the request body
            String name = (String) userData.get("name");
            String phone = (String) userData.get("phone");
            String address = (String) userData.get("address");

            // Call the UserService to register the user in the database
            User user = userService.registerUser(firebaseUid, email, name, phone, address);  // Correctly calling userService.registerUser

            // Return the user details in the response
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "userId", user.getId(),
                    "firebaseUid", user.getFirebaseUid(),
                    "email", user.getEmail(),
                    "name", user.getName()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed: " + e.getMessage());
        }
    }
}
