package com.example.Tech.repos;

import com.example.Tech.entities.Product;
import com.google.api.gax.paging.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product,Long> {
    Optional<Product> findById(Long id);

    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> searchProducts(
            @Param("query") String query,
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice);


    List<Product> findByMainCategory_Id(Long categoryId);


    List<Product> findBySubCategory_Id(Long subCategoryId);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    // Optional: If you want pagination
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);


}
