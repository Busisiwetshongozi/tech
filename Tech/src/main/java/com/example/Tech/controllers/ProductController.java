package com.example.Tech.controllers;

import com.example.Tech.dtos.ProductRequestDTO;
import com.example.Tech.entities.Product;
import com.example.Tech.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        Product created = productService.createProduct(request);
        return ResponseEntity.ok(created);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }


    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // GET BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return ResponseEntity.ok(productService.searchProducts(query, minPrice, maxPrice));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // RESTOCK
    @PutMapping("/{id}/restock")
    public ResponseEntity<Product> restockProduct(
            @PathVariable Long id,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(productService.restockProduct(id, quantity));
    }

    // APPLY DISCOUNT
    @PutMapping("/{id}/discount")
    public ResponseEntity<Product> applyDiscount(
            @PathVariable Long id,
            @RequestParam double percent
    ) {
        return ResponseEntity.ok(productService.applyDiscount(id, percent));
    }

    // FETCH MULTIPLE WITH QUANTITIES
    @PostMapping("/fetch-with-quantity")
    public ResponseEntity<List<Product>> fetchProductsWithQuantity(
            @RequestParam List<Long> productIds,
            @RequestParam List<Integer> quantities
    ) {
        List<Product> products = productService.fetchProductsWithQuantity(productIds, quantities);
        return ResponseEntity.ok(products);
    }
}
