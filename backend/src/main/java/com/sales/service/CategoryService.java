package com.sales.service;

import com.sales.model.Category;
import com.sales.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    public Category create(String name, String description) {
        if (categoryRepository.existsByName(name))
            throw new RuntimeException("Category already exists: " + name);
        Category c = new Category();
        c.setName(name);
        c.setDescription(description);
        return categoryRepository.save(c);
    }

    public Category update(Long id, String name, String description) {
        Category c = getById(id);
        c.setName(name);
        c.setDescription(description);
        return categoryRepository.save(c);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
