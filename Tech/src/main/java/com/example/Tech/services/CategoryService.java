package com.example.Tech.services;

import com.example.Tech.entities.Category;
import com.example.Tech.repos.CategoryRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepo categoryRepo;

    @Autowired
    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    // CREATE or UPDATE
    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    // READ
    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category getCategoryByName(String name) {
        return categoryRepo.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
    }

    // DELETE
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepo.delete(category);
    }

    // BUSINESS LOGIC: Add subcategory
    public Category addSubCategory(Long parentId, Category subCategory) {
        Category parent = getCategoryById(parentId);
        parent.addSubCategory(subCategory);
        return categoryRepo.save(parent); // cascade will persist subcategory
    }

    // BUSINESS LOGIC: Remove subcategory
    public Category removeSubCategory(Long parentId, Long subCategoryId) {
        Category parent = getCategoryById(parentId);
        Category sub = getCategoryById(subCategoryId);
        parent.removeSubCategory(sub);
        return categoryRepo.save(parent);
    }
}

