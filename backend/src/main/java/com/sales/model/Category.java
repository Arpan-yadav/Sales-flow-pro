package com.sales.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Category() {}

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class CategoryBuilder {
        private String name;
        private String description;
        public CategoryBuilder name(String name) { this.name = name; return this; }
        public CategoryBuilder description(String description) { this.description = description; return this; }
        public Category build() {
            Category c = new Category();
            c.setName(name);
            c.setDescription(description);
            return c;
        }
    }
}
