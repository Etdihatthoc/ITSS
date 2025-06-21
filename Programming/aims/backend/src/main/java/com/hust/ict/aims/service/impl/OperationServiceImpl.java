package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.model.Operation;
import com.hust.ict.aims.repository.OperationRepository;
import com.hust.ict.aims.service.OperationService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
/* Cohesion Level: Functional Cohesion
 * Similarly demonstrates functional cohesion, with all members focused on representing an operation performed on a product. The timestamp, operation type, and product reference all support this single purpose.
*/
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

        // Đây là một cách đơn giản để đếm số lần cập nhật
        // Thực tế phức tạp hơn, cần tạo một repository method
        return operationRepository.findAll().stream()
                .filter(op -> op.getProduct() != null && op.getProduct().getId().equals(productId))
                .filter(op -> op.getOperationType().equals(operationType))
                .filter(op -> op.getTimestamp().isAfter(startTime)
                        && op.getTimestamp().isBefore(endTime))
                .count();
    }
}
