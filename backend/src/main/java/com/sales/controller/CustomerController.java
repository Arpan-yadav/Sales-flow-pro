package com.sales.controller;

import com.sales.dto.ApiResponse;
import com.sales.model.Customer;
import com.sales.service.CustomerService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(@RequestParam(required = false) String search) {
        List<Map<String, Object>> result = customerService.getAll(search).stream().map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(toMap(customerService.getById(id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Customer customer) {
        Customer c = customerService.create(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Customer created", toMap(c)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(ApiResponse.success(toMap(customerService.update(id, customer))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted", null));
    }

    private Map<String, Object> toMap(Customer c) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("name", c.getName());
        m.put("email", c.getEmail() != null ? c.getEmail() : "");
        m.put("phone", c.getPhone() != null ? c.getPhone() : "");
        m.put("address", c.getAddress() != null ? c.getAddress() : "");
        m.put("city", c.getCity() != null ? c.getCity() : "");
        m.put("country", c.getCountry() != null ? c.getCountry() : "");
        return m;
    }
}
