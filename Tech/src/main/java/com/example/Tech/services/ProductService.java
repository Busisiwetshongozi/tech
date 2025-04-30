package com.example.Tech.services;

import com.example.Tech.dtos.ProductRequestDTO;
import com.example.Tech.entities.Product;
import com.example.Tech.repos.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductImageService productImageService;

    @Autowired
    public ProductService(ProductRepo productRepo,
                          ProductImageService productImageService) {
        this.productRepo = productRepo;
        this.productImageService = productImageService;
    }

    // CREATE
    public Product createProduct(ProductRequestDTO request) {
        validateProductRequest(request);

        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setModel(request.getModel());
        product.setCategory(request.getCategory().toUpperCase());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCondition(request.getCondition());
        product.setBatteryHealth(request.getBatteryHealth());
        product.setStorage(request.getStorage());
        product.setColor(request.getColor());

        Product savedProduct = productRepo.save(product);

        // Handle images if provided


        return savedProduct;
    }

    // READ
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> null);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepo.findByCategoryIgnoreCase(category);
    }

    public List<Product> searchProducts(String query, Double minPrice, Double maxPrice) {
        return productRepo.searchProducts(
                query.toLowerCase(),
                minPrice != null ? minPrice : 0.0,
                maxPrice != null ? maxPrice : Double.MAX_VALUE
        );
    }



    // DELETE
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepo.delete(product);
    }

    // BUSINESS LOGIC
    public Product restockProduct(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }

        Product product = getProductById(id);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        return productRepo.save(product);
    }

    public Product applyDiscount(Long id, double percentDiscount) {
        if (percentDiscount < 0 || percentDiscount > 100) {
            throw new IllegalArgumentException("Discount must be between 0-100%");
        }

        Product product = getProductById(id);
        double newPrice = product.getPrice() * (1 - (percentDiscount / 100));
        product.setPrice(Math.round(newPrice * 100.0) / 100.0); // Round to 2 decimal places
        return productRepo.save(product);
    }

    // VALIDATION
    private void validateProductRequest(ProductRequestDTO request) {
        if (request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (request.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        if (request.getBatteryHealth() != null &&
                (request.getBatteryHealth() < 0 || request.getBatteryHealth() > 100)) {
            throw new IllegalArgumentException("Battery health must be 0-100%");
        }
    }


}