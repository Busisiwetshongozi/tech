package com.example.Tech.controllers;

import com.example.Tech.services.AuthService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/protected")
public class ApiController {

    private final AuthService authService;

    @Autowired
    public ApiController(AuthService authService) {
        this.authService = authService;
    }

    // ✅ Endpoint to verify Firebase token and return basic UID
    @PostMapping("/user-info")
    public ResponseEntity<Object> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-store");
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.put("status", "error");
            response.put("message", "Authorization header missing or invalid");
            return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
        }

        String idToken = authorizationHeader.substring(7);  // Remove "Bearer "

        try {
            FirebaseToken decodedToken = authService.verifyToken(idToken);

            if (decodedToken != null) {
                response.put("status", "success");
                response.put("message", "Token is valid");
                response.put("userId", decodedToken.getUid());
                return new ResponseEntity<>(response, headers, HttpStatus.OK);
            } else {
                response.put("status", "error");
                response.put("message", "Invalid token");
                return new ResponseEntity<>(response, headers, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Token verification failed: " + e.getMessage());
            return new ResponseEntity<>(response, headers, HttpStatus.UNAUTHORIZED);
        }
    }
}
