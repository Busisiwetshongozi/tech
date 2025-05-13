package com.example.Tech.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // Parent Category Relationship (Managed side)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    @JsonManagedReference
    private Category parentCategory;

    // Subcategories (Back side)
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Category> subCategories = new ArrayList<>();

    // Products assigned to this as the main category (Back side)
    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL)
    @JsonBackReference(value = "main-category")
    private List<Product> mainCategoryProducts = new ArrayList<>();

    // Products assigned to this as the sub category (Back side)
    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL)
    @JsonBackReference(value = "sub-category")
    private List<Product> subCategoryProducts = new ArrayList<>();

    // Constructors
    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public List<Product> getMainCategoryProducts() {
        return mainCategoryProducts;
    }

    public List<Product> getSubCategoryProducts() {
        return subCategoryProducts;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public void setMainCategoryProducts(List<Product> mainCategoryProducts) {
        this.mainCategoryProducts = mainCategoryProducts;
    }

    public void setSubCategoryProducts(List<Product> subCategoryProducts) {
        this.subCategoryProducts = subCategoryProducts;
    }

    // Convenience methods
    public void addSubCategory(Category subCategory) {
        this.subCategories.add(subCategory);
        subCategory.setParentCategory(this);
    }

    public void removeSubCategory(Category subCategory) {
        this.subCategories.remove(subCategory);
        subCategory.setParentCategory(null);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
