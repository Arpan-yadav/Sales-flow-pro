package com.sales.controller;

import com.sales.dto.ApiResponse;
import com.sales.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDashboard()));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRevenue(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getRevenueTrend(days)));
    }

    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopProducts(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getTopProducts(limit)));
    }

    @GetMapping("/salesperson")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSalespersonPerformance() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSalespersonPerformance()));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRevenueByCategory() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getRevenueByCategory()));
    }
}
