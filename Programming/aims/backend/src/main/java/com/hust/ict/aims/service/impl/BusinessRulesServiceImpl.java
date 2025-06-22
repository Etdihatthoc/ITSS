package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.service.BusinessRulesService;
import com.hust.ict.aims.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BusinessRulesServiceImpl implements BusinessRulesService {
    
    private final OperationService operationService;
    private final ConcurrentHashMap<String, ReentrantLock> operationLocks;
    
    // Business rule constants
    private static final int MAX_DAILY_UPDATES_DELETES = 30;
    private static final int MAX_BULK_DELETE = 10;
    
    @Autowired
    public BusinessRulesServiceImpl(OperationService operationService) {
        this.operationService = operationService;
        this.operationLocks = new ConcurrentHashMap<>();
        // Initialize locks for different operation types
        operationLocks.put("ADD_PRODUCT", new ReentrantLock());
        operationLocks.put("UPDATE_PRODUCT", new ReentrantLock());
    }
    
    @Override
    public void validateDailyOperationLimits(String operationType, LocalDateTime date) {
        if ("UPDATE_PRODUCT".equals(operationType) || "DELETE_PRODUCT".equals(operationType)) {
            LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
            
            long updateCount = operationService.countByProductIdAndOperationTypeAndTimestampBetween(
                    null, "UPDATE_PRODUCT", startOfDay, endOfDay);
            long deleteCount = operationService.countByProductIdAndOperationTypeAndTimestampBetween(
                    null, "DELETE_PRODUCT", startOfDay, endOfDay);
            
            if (updateCount + deleteCount >= MAX_DAILY_UPDATES_DELETES) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Daily limit exceeded: Cannot perform more than " + MAX_DAILY_UPDATES_DELETES + 
                        " update/delete operations per day for security reasons"
                );
            }
        }
    }
    
    @Override
    public void acquireOperationLock(String operationType) {
        if ("ADD_PRODUCT".equals(operationType) || "UPDATE_PRODUCT".equals(operationType)) {
            ReentrantLock lock = operationLocks.get(operationType);
            if (lock != null && !lock.tryLock()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Another " + operationType.toLowerCase().replace("_", " ") + 
                        " operation is in progress. Only one product can be added/edited at a time."
                );
            }
        }
    }
    
    @Override
    public void releaseOperationLock(String operationType) {
        if ("ADD_PRODUCT".equals(operationType) || "UPDATE_PRODUCT".equals(operationType)) {
            ReentrantLock lock = operationLocks.get(operationType);
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    @Override
    public boolean canPerformBulkDelete(int count) {
        return count <= MAX_BULK_DELETE;
    }
}