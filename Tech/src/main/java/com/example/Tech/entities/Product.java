package com.example.Tech.entities;


import com.example.Tech.enums.Condition;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
public class Product {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;          // "iPhone 12 128GB"
        private String brand;         // "Apple"
        private String model;         // "iPhone 12"
        private String category;      // "PHONE", "LAPTOP"
        private String description;   // "Refurbished, like new"
        private double price;
        private int stockQuantity;

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
        @JsonManagedReference //
        private List<Review> reviews;

        @Enumerated(EnumType.STRING)
        private Condition condition;  // NEW, REFURBISHED, USED_GOOD

        private int batteryHealth;    // 85 (percentage)
        private String storage;       // "128GB"
        private String color;         // "Black"

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
        @Column(name = "image_url", length = 2048) // Extra length for long URLs
        private List<String> imageUrls = new ArrayList<>();

        // Convenience methods for image management
        public void addImageUrl(String url) {
                if (url != null && !url.isBlank()) {
                        this.imageUrls.add(url);
                }
        }

        public void removeImageUrl(String url) {
                this.imageUrls.remove(url);
        }

        public void clearAllImages() {
                this.imageUrls.clear();
        }

        // Getters, setters, constructors
        public Long getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public String getBrand() {
                return brand;
        }

        public String getModel() {
                return model;
        }

        public String getCategory() {
                return category;
        }

        public String getDescription() {
                return description;
        }

        public double getPrice() {
                return price;
        }

        public int getStockQuantity() {
                return stockQuantity;
        }

        public Condition getCondition() {
                return condition;
        }

        public int getBatteryHealth() {
                return batteryHealth;
        }

        public String getStorage() {
                return storage;
        }

        public String getColor() {
                return color;
        }



        // Setters
        public void setId(Long id) {
                this.id = id;
        }

        public void setName(String name) {
                this.name = name;
        }

        public void setBrand(String brand) {
                this.brand = brand;
        }

        public void setModel(String model) {
                this.model = model;
        }

        public void setCategory(String category) {
                this.category = category;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public void setPrice(double price) {
                this.price = price;
        }

        public void setStockQuantity(int stockQuantity) {
                this.stockQuantity = stockQuantity;
        }

        public void setCondition(Condition condition) {
                this.condition = condition;
        }

        public void setBatteryHealth(int batteryHealth) {
                this.batteryHealth = batteryHealth;
        }

        public void setStorage(String storage) {
                this.storage = storage;
        }

        public void setColor(String color) {
                this.color = color;
        }



        // Convenience method for adding images


        // Convenience method for removing images



}

