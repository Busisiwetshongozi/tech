package com.example.Tech.entities;

import com.example.Tech.enums.Condition;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id")
    private Category mainCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private Category subCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price; // <-- changed from double to BigDecimal

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Condition condition;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @Transient
    @JsonProperty("discountedPrice")
    public BigDecimal getDiscountedPrice() {
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountFraction = discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal discountAmount = price.multiply(discountFraction);
            return price.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }


    @Column(name = "battery_health")
    private Integer batteryHealth;

    @Column(nullable = false)
    private String storage;

    private String color;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_specs", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "spec_key")
    @Column(name = "spec_value")
    private Map<String, String> specifications = new HashMap<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    public Product() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Product(String name, String brand, String model) {
        this();
        this.name = name;
        this.brand = brand;
        this.model = model;
    }

    // Getters
    public Long getId() { return id; }

    public String getName() { return name; }

    public String getBrand() { return brand; }

    public String getModel() { return model; }

    public Category getMainCategory() { return mainCategory; }

    public Category getSubCategory() { return subCategory; }

    public String getDescription() { return description; }

    public BigDecimal getPrice() { return price; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }

    public int getStockQuantity() { return stockQuantity; }

    public List<Review> getReviews() { return reviews; }

    public Condition getCondition() { return condition; }

    public Integer getBatteryHealth() { return batteryHealth; }

    public String getStorage() { return storage; }

    public String getColor() { return color; }

    public Map<String, String> getSpecifications() { return specifications; }

    public List<ProductImage> getImages() { return images; }

    public Long getCreatedAt() { return createdAt; }

    public Long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setBrand(String brand) {
        this.brand = brand;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setModel(String model) {
        this.model = model;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setMainCategory(Category mainCategory) {
        this.mainCategory = mainCategory;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setSubCategory(Category subCategory) {
        this.subCategory = subCategory;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setBatteryHealth(Integer batteryHealth) {
        this.batteryHealth = batteryHealth;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setStorage(String storage) {
        this.storage = storage;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setColor(String color) {
        this.color = color;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setSpecifications(Map<String, String> specifications) {
        this.specifications = specifications;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
        this.updatedAt = System.currentTimeMillis();
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setProduct(this);
        this.updatedAt = System.currentTimeMillis();
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setProduct(null);
        this.updatedAt = System.currentTimeMillis();
    }

    public void addImage(ProductImage image) {
        if (image != null) {
            this.images.add(image);
            image.setProduct(this);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public void removeImage(ProductImage image) {
        if (image != null) {
            this.images.remove(image);
            image.setProduct(null);
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public void clearAllImages() {
        this.images.clear();
        this.updatedAt = System.currentTimeMillis();
    }

    public void addSpecification(String key, String value) {
        this.specifications.put(key, value);
        this.updatedAt = System.currentTimeMillis();
    }

    public void removeSpecification(String key) {
        this.specifications.remove(key);
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isInStock() {
        return this.stockQuantity > 0;
    }

    public boolean isRefurbished() {
        return this.condition == Condition.REFURBISHED;
    }

    public boolean hasBattery() {
        return this.batteryHealth != null;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", price=" + price +
                '}';
    }
}
