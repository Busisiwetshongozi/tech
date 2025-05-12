package com.example.Tech.repos;

import com.example.Tech.entities.Product;
import com.example.Tech.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review,Long> {
    List<Review> findByProduct(Product product);
}
