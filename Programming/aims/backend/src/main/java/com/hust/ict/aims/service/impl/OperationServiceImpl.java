package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.model.Operation;
import com.hust.ict.aims.repository.OperationRepository;
import com.hust.ict.aims.service.OperationService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationServiceImpl implements OperationService {
    private final OperationRepository operationRepository;

    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public Operation save(Operation operation) {
        return operationRepository.save(operation);
    }

    @Override
    public Operation findById(Long id) {
        return operationRepository.findById(id).orElse(null);
    }

    @Override
    public List<Operation> findAll() {
        return operationRepository.findAll();
    }

    @Override
    public Operation update(Long id, Operation operation) {
        if (!operationRepository.existsById(id)) {
            return null;
        }
        operation.setOperationID(id);
        return operationRepository.save(operation);
    }

    @Override
    public void delete(Long id) {
        operationRepository.deleteById(id);
    }

    @Override
    public long countByProductIdAndOperationTypeAndTimestampBetween(
            Long productId, String operationType,
            LocalDateTime startTime, LocalDateTime endTime) {

        // Use the repository method instead of filtering in Java
        return operationRepository.countByProductIdAndOperationTypeAndTimestampBetween(
                productId, operationType, startTime, endTime);
    }

    // IMPLEMENT THE NEW METHODS
    @Override
    public List<Operation> findAllOrderByTimestamp() {
        return operationRepository.findAllOrderByTimestampDesc();
    }

    @Override
    public Page<Operation> findOperationsWithFilters(String search, String operationType, 
                                                     int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return operationRepository.findOperationsWithFilters(search, operationType, pageable);
    }

    @Override
    public List<Operation> findByProductId(Long productId) {
        return operationRepository.findByProductId(productId);
    }
}