package com.sales.controller;

import com.sales.dto.ApiResponse;
import com.sales.model.Product;
import com.sales.service.ProductService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long category) {
        List<Product> products = productService.getAll(search, category);
        List<Map<String, Object>> result = products.stream().map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(toMap(productService.getById(id))));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLowStock() {
        List<Map<String, Object>> result = productService.getLowStock().stream().map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @RequestBody ProductService.ProductRequest req) {
        Product p = productService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Product created", toMap(p)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(
            @PathVariable Long id,
            @RequestBody ProductService.ProductRequest req) {
        return ResponseEntity.ok(ApiResponse.success(toMap(productService.update(id, req))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() throws Exception {
        byte[] csv = productService.exportCsv();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"products.csv\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csv);
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> importCsv(@RequestParam("file") MultipartFile file) throws Exception {
        int count = productService.importCsv(file);
        return ResponseEntity.ok(ApiResponse.success("Imported " + count + " products", null));
    }

    private Map<String, Object> toMap(Product p) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("id",                p.getId());
        m.put("name",              p.getName());
        m.put("sku",               p.getSku() != null ? p.getSku() : "");
        m.put("description",       p.getDescription() != null ? p.getDescription() : "");
        m.put("price",             p.getPrice());
        m.put("stockQuantity",     p.getStockQuantity());
        m.put("lowStockThreshold", p.getLowStockThreshold());
        m.put("lowStock",          p.isLowStock());
        m.put("categoryId",        p.getCategory() != null ? p.getCategory().getId() : null);
        m.put("categoryName",      p.getCategory() != null ? p.getCategory().getName() : "");
        return m;
    }
}
