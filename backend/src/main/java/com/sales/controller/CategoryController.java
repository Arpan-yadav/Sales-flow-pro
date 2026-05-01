package com.sales.controller;

import com.sales.dto.ApiResponse;
import com.sales.model.Category;
import com.sales.service.CategoryService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody CategoryRequest req) {
        Category c = categoryService.create(req.getName(), req.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Category created", c));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> update(@PathVariable Long id, @RequestBody CategoryRequest req) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.update(id, req.getName(), req.getDescription())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }

    public static class CategoryRequest {
        private String name;
        private String description;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
