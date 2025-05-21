package com.example.Tech.repos;

import com.example.Tech.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageStorageRepo extends JpaRepository<ProductImage,Long> {
}
