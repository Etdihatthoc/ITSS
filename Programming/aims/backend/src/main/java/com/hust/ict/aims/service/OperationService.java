package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Operation;
import java.util.List;
import java.time.LocalDateTime;
/* Cohesion Level: Functional Cohesion 
 * Similarly demonstrates functional cohesion, with all members focused on representing an operation performed on a product. The timestamp, operation type, and product reference all support this single purpose.
*/
public interface OperationService {
    Operation save(Operation operation);
    Operation findById(Long id);
    List<Operation> findAll();
    Operation update(Long id, Operation operation);
    void delete(Long id);
    long countByProductIdAndOperationTypeAndTimestampBetween(
            Long productId, String operationType,
            LocalDateTime startTime, LocalDateTime endTime);
}
