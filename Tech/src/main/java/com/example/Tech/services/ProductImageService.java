package com.example.Tech.services;

import com.example.Tech.entities.Product;
import com.example.Tech.entities.ProductImage;
import com.example.Tech.repos.ProductImageRepo;
import com.example.Tech.repos.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ProductImageService {

    private final ProductImageRepo productImageRepo;
    private final ProductRepo productRepo;


    @Autowired
    public ProductImageService(ProductImageRepo productImageRepo,
                               ProductRepo productRepo) {
        this.productImageRepo = productImageRepo;
        this.productRepo = productRepo;

    }

    // Create single product image
    public ProductImage createProductImage(Long productId, String imageUrl) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> null);

        ProductImage productImage = new ProductImage();
        productImage.setImageUrl(imageUrl);
        productImage.setProduct(product);

        return productImageRepo.save(productImage);
    }

    // Batch create product images
    public List<ProductImage> createProductImages(Product product, List<String> imageUrls) {
        return imageUrls.stream()
                .map(url -> {
                    ProductImage image = new ProductImage();
                    image.setImageUrl(url);
                    image.setProduct(product);
                    return productImageRepo.save(image);
                })
                .collect(Collectors.toList());
    }

    // Get product image by ID
    public ProductImage getProductImageById(Long id) {
        return productImageRepo.findById(id)
                .orElseThrow(() -> null);
    }

    public List<ProductImage> getImagesByProductId(Long productId) {

        return productImageRepo.findByProductId(productId);
    }




}
