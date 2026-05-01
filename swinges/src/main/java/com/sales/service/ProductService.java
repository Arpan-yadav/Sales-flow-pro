package com.sales.service;

import com.sales.model.Product;
import com.sales.model.Category;
import com.sales.repository.ProductRepository;
import com.sales.repository.CategoryRepository;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAll(String search, Long categoryId) {
        if (search != null && !search.isBlank())
            return productRepository.searchProducts(search.trim());
        if (categoryId != null)
            return productRepository.findByCategoryIdAndActiveTrue(categoryId);
        return productRepository.findByActiveTrue();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public List<Product> getLowStock() {
        return productRepository.findLowStockProducts();
    }

    public Product create(ProductRequest req) {
        if (req.getSku() != null && !req.getSku().isBlank() && productRepository.existsBySku(req.getSku()))
            throw new RuntimeException("SKU already exists: " + req.getSku());

        Product p = new Product();
        mapRequest(p, req);
        return productRepository.save(p);
    }

    public Product update(Long id, ProductRequest req) {
        Product p = getById(id);
        mapRequest(p, req);
        return productRepository.save(p);
    }

    public void delete(Long id) {
        Product p = getById(id);
        p.setActive(false);
        productRepository.save(p);
    }

    public long count() { return productRepository.count(); }
    public long countLowStock() { return productRepository.findLowStockProducts().size(); }

    public byte[] exportCsv() throws IOException {
        List<Product> products = productRepository.findByActiveTrue();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(
                new OutputStreamWriter(out, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.withHeader("ID","Name","SKU","Category","Price","Stock","LowStockThreshold","Active"))) {
            for (Product p : products) {
                printer.printRecord(
                    p.getId(), p.getName(), p.getSku(),
                    p.getCategory() != null ? p.getCategory().getName() : "",
                    p.getPrice(), p.getStockQuantity(), p.getLowStockThreshold(), p.isActive()
                );
            }
        }
        return out.toByteArray();
    }

    public int importCsv(MultipartFile file) throws IOException {
        int count = 0;
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            for (CSVRecord row : parser) {
                String name = row.get("Name");
                String sku  = row.isMapped("SKU") ? row.get("SKU") : null;
                if (sku != null && productRepository.existsBySku(sku)) continue;

                Product p = new Product();
                p.setName(name);
                p.setSku(sku);
                p.setPrice(new BigDecimal(row.isMapped("Price") ? row.get("Price") : "0"));
                p.setStockQuantity(Integer.parseInt(row.isMapped("Stock") ? row.get("Stock") : "0"));
                p.setActive(true);

                if (row.isMapped("Category")) {
                    String catName = row.get("Category");
                    Category cat = categoryRepository.findByName(catName)
                        .orElseGet(() -> {
                            Category newCat = new Category();
                            newCat.setName(catName);
                            return categoryRepository.save(newCat);
                        });
                    p.setCategory(cat);
                }
                productRepository.save(p);
                count++;
            }
        }
        return count;
    }

    private void mapRequest(Product p, ProductRequest req) {
        p.setName(req.getName());
        p.setSku(req.getSku());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStockQuantity(req.getStockQuantity());
        p.setLowStockThreshold(req.getLowStockThreshold() != null ? req.getLowStockThreshold() : 10);
        p.setActive(true);
        if (req.getCategoryId() != null) {
            categoryRepository.findById(req.getCategoryId()).ifPresent(p::setCategory);
        }
    }

    public static class ProductRequest {
        private String name;
        private String sku;
        private String description;
        private BigDecimal price;
        private Integer stockQuantity;
        private Integer lowStockThreshold;
        private Long categoryId;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public Integer getLowStockThreshold() { return lowStockThreshold; }
        public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    }
}
