package com.example.Tech.dtos;

import com.example.Tech.enums.Condition;
import jakarta.validation.constraints.*;

import java.util.List;

public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    private String name;

    private Long mainCategoryId;
    private Long subCategoryId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;  // This is the ID of the category

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Condition is required")
    private Condition condition;

    @Min(value = 0, message = "Battery health cannot be less than 0")
    @Max(value = 100, message = "Battery health cannot be more than 100")
    private Integer batteryHealth;

    @NotBlank(message = "Storage is required")
    private String storage;

    @NotBlank(message = "Color is required")
    private String color;

    private List<@NotBlank(message = "Image URL cannot be blank") String> imageUrls;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getMainCategoryId() { return mainCategoryId; }
    public void setMainCategoryId(Long mainCategoryId) { this.mainCategoryId = mainCategoryId; }

    public Long getSubCategoryId() { return subCategoryId; }
    public void setSubCategoryId(Long subCategoryId){ this.subCategoryId = subCategoryId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }

    public Integer getBatteryHealth() { return batteryHealth; }
    public void setBatteryHealth(Integer batteryHealth) { this.batteryHealth = batteryHealth; }

    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
