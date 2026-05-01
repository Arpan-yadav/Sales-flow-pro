package com.sales.controller;

import com.sales.dto.ApiResponse;
import com.sales.model.Order;
import com.sales.model.enums.OrderStatus;
import com.sales.service.InvoiceService;
import com.sales.service.OrderService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;

    public OrderController(OrderService orderService, InvoiceService invoiceService) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long customer,
            @RequestParam(required = false) Long salesperson) {
        List<Map<String, Object>> result = orderService.getAll(status, customer, salesperson)
            .stream().map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(toMap(orderService.getById(id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody OrderService.OrderCreateRequest req) {
        Order o = orderService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Order created", toMap(o)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderService.StatusRequest req) {
        Order o = orderService.updateStatus(id, req.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Status updated to " + req.getStatus(), toMap(o)));
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        Order order = orderService.getById(id);
        byte[] pdf = invoiceService.generatePdf(order);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice_" + id + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    private Map<String, Object> toMap(Order o) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("id", o.getId());
        m.put("customerName", o.getCustomer().getName());
        m.put("customerId", o.getCustomer().getId());
        m.put("salespersonName", o.getSalesperson().getName());
        m.put("status", o.getStatus());
        m.put("subtotal", o.getSubtotal());
        m.put("discountAmount", o.getDiscountAmount());
        m.put("taxAmount", o.getTaxAmount());
        m.put("total", o.getTotal());
        m.put("createdAt", o.getCreatedAt());
        m.put("notes", o.getNotes());
        
        m.put("customerEmail", o.getCustomer().getEmail());

        if (o.getInvoice() != null) {
            Map<String, Object> inv = new java.util.HashMap<>();
            inv.put("id", o.getInvoice().getId());
            inv.put("number", o.getInvoice().getInvoiceNumber());
            m.put("invoice", inv);
        }

        m.put("items", o.getItems().stream().map(it -> {
            Map<String, Object> im = new java.util.HashMap<>();
            im.put("id", it.getId());
            im.put("productId", it.getProduct().getId());
            im.put("productName", it.getProduct().getName());
            im.put("quantity", it.getQuantity());
            im.put("unitPrice", it.getUnitPrice());
            im.put("lineTotal", it.getLineTotal());
            return im;
        }).toList());

        return m;
    }
}
