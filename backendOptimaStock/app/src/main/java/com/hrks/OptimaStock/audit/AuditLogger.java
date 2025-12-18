package com.hrks.OptimaStock.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Specialized logger for audit trail of critical operations
 */
@Component
public class AuditLogger {

    private static final Logger auditLog = LoggerFactory.getLogger("com.hrks.OptimaStock.audit");

    /**
     * Log user authentication events
     */
    public void logAuthentication(String username, String action, boolean success) {
        if (success) {
            auditLog.info("AUTHENTICATION | User: {} | Action: {} | Status: SUCCESS", username, action);
        } else {
            auditLog.warn("AUTHENTICATION | User: {} | Action: {} | Status: FAILED", username, action);
        }
    }

    /**
     * Log sale operations
     */
    public void logSale(Integer saleId, String username, Double total, String action) {
        auditLog.info("SALE | SaleID: {} | User: {} | Total: {} | Action: {}",
                saleId, username, total, action);
    }

    /**
     * Log inventory movements
     */
    public void logInventoryMovement(Integer movementId, Integer productId, Integer quantity,
            String movementType, String username) {
        auditLog.info("INVENTORY | MovementID: {} | ProductID: {} | Quantity: {} | Type: {} | User: {}",
                movementId, productId, quantity, movementType, username);
    }

    /**
     * Log data modification operations
     */
    public void logDataModification(String entity, Integer entityId, String action, String username) {
        auditLog.info("DATA_MODIFICATION | Entity: {} | EntityID: {} | Action: {} | User: {}",
                entity, entityId, action, username);
    }

    /**
     * Log security events
     */
    public void logSecurityEvent(String event, String username, String details) {
        auditLog.warn("SECURITY | Event: {} | User: {} | Details: {}", event, username, details);
    }

    /**
     * Log critical errors
     */
    public void logCriticalError(String operation, String error, String details) {
        auditLog.error("CRITICAL_ERROR | Operation: {} | Error: {} | Details: {}",
                operation, error, details);
    }
}
