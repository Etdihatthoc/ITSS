package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Make sure this import is correct
import java.time.LocalDateTime;

@Service // Add this annotation
public class BusinessRuleValidator {

    @Autowired
    private OperationService operationService;

    public boolean canExecuteOperation(Long productId, String operationType) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return switch (operationType) {
            case "ADD_PRODUCT" -> true; // Unlimited additions
            case "UPDATE_PRODUCT" -> {
                if (productId == null) yield true; // For new products
                long updateCount = operationService.countByProductIdAndOperationTypeAndTimestampBetween(
                        productId, "UPDATE_PRODUCT", startOfDay, endOfDay);
                yield updateCount < 30; // Max 30 updates per day
            }
            case "DELETE_PRODUCT" -> {
                if (productId == null) yield true; // For new products
                long deleteCount = operationService.countByProductIdAndOperationTypeAndTimestampBetween(
                        productId, "DELETE_PRODUCT", startOfDay, endOfDay);
                yield deleteCount < 30; // Max 30 deletes per day
            }
            default -> false;
        };
    }

    public String getFailureReason(String operationType) {
        return switch (operationType) {
            case "UPDATE_PRODUCT" -> "Maximum 30 product updates per day exceeded";
            case "DELETE_PRODUCT" -> "Maximum 30 product deletions per day exceeded";
            default -> "Operation not allowed";
        };
    }
}