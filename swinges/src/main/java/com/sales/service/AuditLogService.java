package com.sales.service;

import com.sales.model.AuditLog;
import com.sales.repository.AuditLogRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long userId, String username, String action, String entity, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setEntity(entity);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAll() {
        return auditLogRepository.findAll(Sort.by("timestamp").descending());
    }

    public Page<AuditLog> getAll(int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return auditLogRepository.findAll(p);
    }
}
