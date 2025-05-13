package com.example.Tech.controllers;

import com.example.Tech.entities.Category;
import com.example.Tech.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    // READ
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/name/{name}")
    public Category getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    // ADD SUBCATEGORY
    @PostMapping("/{parentId}/subcategories")
    public Category addSubCategory(@PathVariable Long parentId, @RequestBody Category subCategory) {
        return categoryService.addSubCategory(parentId, subCategory);
    }

    // REMOVE SUBCATEGORY
    @DeleteMapping("/{parentId}/subcategories/{subCategoryId}")
    public Category removeSubCategory(@PathVariable Long parentId, @PathVariable Long subCategoryId) {
        return categoryService.removeSubCategory(parentId, subCategoryId);
    }
}
