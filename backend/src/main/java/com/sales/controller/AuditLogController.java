package com.sales.controller;

import com.sales.dto.ApiResponse;
import com.sales.model.AuditLog;
import com.sales.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditLog> result = auditLogService.getAll(page, size);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("content",       result.getContent());
        m.put("totalElements", result.getTotalElements());
        m.put("totalPages",    result.getTotalPages());
        m.put("page",          result.getNumber());
        return ResponseEntity.ok(ApiResponse.success(m));
    }
}
