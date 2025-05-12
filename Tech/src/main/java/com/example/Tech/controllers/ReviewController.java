package com.example.Tech.controllers;

import com.example.Tech.dtos.ReviewDTO;
import com.example.Tech.entities.Review;
import com.example.Tech.entities.User;
import com.example.Tech.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<?> createReview(
            @RequestBody ReviewDTO reviewDTO,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Extract Firebase token from header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String firebaseToken = authHeader.substring(7);
            Review review = reviewService.createReview(reviewDTO, firebaseToken);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Other endpoints...
    @GetMapping("{productId}/reviews")
    public List<ReviewDTO> getReviewsForProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProductId(productId);
    }


}

