package com.sales.repository;

import com.sales.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity <= p.lowStockThreshold")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProducts(String query);
}
