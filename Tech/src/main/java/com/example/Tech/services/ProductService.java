package com.example.Tech.services;

import com.example.Tech.dtos.ProductRequestDTO;
import com.example.Tech.entities.Category;
import com.example.Tech.entities.Product;
import com.example.Tech.entities.ProductImage;
import com.example.Tech.repos.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final ImageStorageService imageStorageService;
    @Autowired
    public ProductService(ProductRepo productRepo,ImageStorageService imageStorageService, ProductImageService productImageService,CategoryService categoryService) {
        this.productRepo = productRepo;
        this.productImageService = productImageService;
        this.categoryService=categoryService;
        this.imageStorageService=imageStorageService;
    }

    public Product createProductWithImages(ProductRequestDTO request, List<MultipartFile> images) {
        validateProductRequest(request);

        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setModel(request.getModel());

        if (request.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(request.getCategoryId());
            if (category == null) {
                throw new IllegalArgumentException("Category not found");
            }
            product.setMainCategory(category);
        } else {
            throw new IllegalArgumentException("Category ID is required.");
        }

        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCondition(request.getCondition());
        product.setBatteryHealth(request.getBatteryHealth());
        product.setStorage(request.getStorage());
        product.setColor(request.getColor());

        // ✅ Save images and link via ProductImage entity
        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = imageStorageService.storeImages(images);
            for (String imageUrl : imageUrls) {
                ProductImage image = new ProductImage();
                image.setImageUrl(imageUrl);
                image.setProduct(product);
                product.addImage(image); // convenience method in Product
            }
        }

        return productRepo.save(product);
    }



    // READ
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepo.findByMainCategory_Id(categoryId);
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

    // BUSINESS LOGIC: Fetch Multiple Products with Quantity Check
    public List<Product> fetchProductsWithQuantity(List<Long> productIds, List<Integer> quantities) {
        if (productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Product IDs and quantities must match");
        }

        List<Product> products = productIds.stream()
                .map(productId -> productRepo.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId)))
                .collect(Collectors.toList());

        // Check if the requested quantity is less than or equal to the available stock for each product
        for (int i = 0; i < productIds.size(); i++) {
            Product product = products.get(i);
            int requestedQuantity = quantities.get(i);

            if (product.getStockQuantity() < requestedQuantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Deduct the quantity from the stock
            product.setStockQuantity(product.getStockQuantity() - requestedQuantity);
            productRepo.save(product);  // Save the updated product with reduced stock
        }

        return products;  // Return the list of products that were successfully fetched
    }

    // RESTOCK PRODUCT
    public Product restockProduct(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }

        Product product = getProductById(id);
        product.setStockQuantity(product.getStockQuantity() + quantity);
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
    public Product applyDiscount(Long productId, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        Product product = getProductById(productId);
        product.setDiscountPercentage(discountPercent);
        return productRepo.save(product);
    }

}