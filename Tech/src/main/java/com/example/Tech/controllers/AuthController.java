package com.example.Tech.controllers;

import com.example.Tech.dtos.UserRegistrationDTO;
import com.example.Tech.entities.User;
import com.example.Tech.services.AuthService;
import com.example.Tech.services.UserService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserRegistrationDTO userData
    ) {
        Map<String, Object> response = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-store");
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Authorization header missing or invalid");
            return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
        }

        String idToken = authorizationHeader.substring(7);

        try {
            FirebaseToken decodedToken = authService.verifyToken(idToken);

            if (decodedToken == null) {
                response.put("status", "error");
                response.put("message", "Invalid token");
                return new ResponseEntity<>(response, headers, HttpStatus.UNAUTHORIZED);
            }

            // ✅ Build the actual User entity from the DTO
            User user = new User();
            user.setFirebaseUid(decodedToken.getUid());
            user.setEmail(userData.getEmail());
            user.setName(userData.getName() != null ? userData.getName() : decodedToken.getName());
            user.setPhone(userData.getPhone());
            user.setAddress(userData.getAddress());
            user.setEnabled(true);

            User savedUser = userService.registerUser(user);

            response.put("status", "success");
            response.put("user", savedUser);
            return new ResponseEntity<>(response, headers, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Registration failed: " + e.getMessage());
            return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Map<String, Object> response = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-store");
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Authorization header missing or invalid");
            return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
        }

        String idToken = authorizationHeader.substring(7);

        try {
            FirebaseToken decodedToken = authService.verifyToken(idToken);

            if (decodedToken == null) {
                response.put("status", "error");
                response.put("message", "Invalid token");
                return new ResponseEntity<>(response, headers, HttpStatus.UNAUTHORIZED);
            }

            // Get user from database using Firebase UID
            User user = userService.getUserByFirebaseUid(decodedToken.getUid());

            if (user == null) {
                response.put("status", "error");
                response.put("message", "User not found in database");
                return new ResponseEntity<>(response, headers, HttpStatus.NOT_FOUND);
            }

            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("user", user);
            return new ResponseEntity<>(response, headers, HttpStatus.OK);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch user: " + e.getMessage());
            return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}