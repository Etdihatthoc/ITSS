package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Operation;
import java.time.LocalDateTime;

public interface BusinessRulesService {
    void validateDailyOperationLimits(String operationType, LocalDateTime date);
    void acquireOperationLock(String operationType);
    void releaseOperationLock(String operationType);
    boolean canPerformBulkDelete(int count);
}