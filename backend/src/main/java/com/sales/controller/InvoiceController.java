package com.sales.controller;

import com.sales.dto.ApiResponse;
import com.sales.model.Invoice;
import com.sales.service.InvoiceService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        List<Map<String, Object>> result = invoiceService.getAll().stream().map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(toMap(invoiceService.getById(id))));
    }

    @PostMapping("/generate/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generate(@PathVariable Long orderId) {
        Invoice inv = invoiceService.generate(orderId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Invoice generated", toMap(inv)));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        byte[] pdf  = invoiceService.download(id);
        Invoice inv = invoiceService.getById(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + inv.getInvoiceNumber() + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    private Map<String, Object> toMap(Invoice inv) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",            inv.getId());
        m.put("invoiceNumber", inv.getInvoiceNumber());
        m.put("orderId",       inv.getOrder().getId());
        m.put("customerName",  inv.getOrder().getCustomer().getName());
        m.put("orderTotal",    inv.getOrder().getTotal());
        m.put("issuedAt",      inv.getIssuedAt());
        return m;
    }
}
