package com.example.Tech.services;

import com.example.Tech.entities.Product;
import com.example.Tech.entities.Review;
import com.example.Tech.entities.User;
import com.example.Tech.dtos.ReviewDTO;
import com.example.Tech.repos.ProductRepo;
import com.example.Tech.repos.ReviewRepo;
import com.example.Tech.repos.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Transactional
    public Review createReview(ReviewDTO reviewDTO, String firebaseToken) {
        // Validate DTO
        if (reviewDTO.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        // Verify Firebase token and get UID
        FirebaseToken decodedToken;
        try {
            decodedToken = firebaseAuth.verifyIdToken(firebaseToken);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid Firebase token", e);
        }

        String firebaseUid = decodedToken.getUid();

        // Find user by Firebase UID
        User currentUser = userRepo.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found for Firebase UID: " + firebaseUid));

        // Find the product with null check
        Product product = productRepo.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + reviewDTO.getProductId()));

        // Create and save the review
        Review review = new Review();
        review.setContent(reviewDTO.getContent());
        review.setRating(reviewDTO.getRating());
        review.setProduct(product);
        review.setUser(currentUser);


        return reviewRepo.save(review);
    }

    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        List<Review> reviews = reviewRepo.findByProduct(product);

        return reviews.stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setId(review.getId());
            dto.setContent(review.getContent());
            dto.setRating(review.getRating());
            dto.setProductId(productId);
            dto.setName(review.getUser().getName()); // ✅ Pull from user
            return dto;
        }).collect(Collectors.toList());
    }

    public ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setProductId(review.getProduct().getId());

        // Get username from user entity (e.g., first name)
        User user = review.getUser();
        if (user != null) {
            dto.setName(user.getName()); // Or getFullName(), getEmail(), etc.
        }

        return dto;
    }

}