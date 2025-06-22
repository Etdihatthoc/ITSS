package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Operation;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;

public interface OperationService {
    Operation save(Operation operation);
    Operation findById(Long id);
    List<Operation> findAll();
    Operation update(Long id, Operation operation);
    void delete(Long id);
    long countByProductIdAndOperationTypeAndTimestampBetween(
            Long productId, String operationType,
            LocalDateTime startTime, LocalDateTime endTime);
    
    // ADD THESE NEW METHODS
    List<Operation> findAllOrderByTimestamp();
    Page<Operation> findOperationsWithFilters(String search, String operationType, 
                                             int page, int size);
    List<Operation> findByProductId(Long productId);
}