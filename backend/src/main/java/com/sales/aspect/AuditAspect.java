package com.sales.aspect;

import com.sales.security.UserDetailsImpl;
import com.sales.service.AuditLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    private final AuditLogService auditLogService;

    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Pointcut("execution(* com.sales.service.*.create*(..)) || " +
              "execution(* com.sales.service.*.update*(..)) || " +
              "execution(* com.sales.service.*.delete*(..))")
    public void auditMethods() {}

    @AfterReturning(pointcut = "auditMethods()", returning = "result")
    public void logAction(JoinPoint joinPoint, Object result) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl userDetails)) {
                return;
            }

            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String entity = className.replace("Service", "");
            
            String action = methodName.startsWith("create") ? "CREATE" :
                           methodName.startsWith("update") ? "UPDATE" : "DELETE";

            Long entityId = null;
            try {
                Method getId = result.getClass().getMethod("getId");
                entityId = (Long) getId.invoke(result);
            } catch (Exception e) {
                // Ignore
            }

            String details = String.format("%s %s via %s", action, entity, methodName);
            
            auditLogService.log(
                userDetails.getId(),
                userDetails.getUsername(),
                action,
                entity,
                entityId,
                details
            );
            
            log.info("Audit logged: {} {} by {}", action, entity, userDetails.getUsername());
        } catch (Exception e) {
            log.error("Failed to log audit: {}", e.getMessage());
        }
    }
}
