package com.hust.ict.aims.repository;

import com.hust.ict.aims.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    
    // Find operations by product ID
    @Query("SELECT o FROM Operation o WHERE o.product.id = :productId ORDER BY o.timestamp DESC")
    List<Operation> findByProductId(@Param("productId") Long productId);
    
    // Find operations by product ID with pagination
    @Query("SELECT o FROM Operation o WHERE o.product.id = :productId ORDER BY o.timestamp DESC")
    Page<Operation> findByProductId(@Param("productId") Long productId, Pageable pageable);
    
    // Find operations by type
    @Query("SELECT o FROM Operation o WHERE o.operationType = :operationType ORDER BY o.timestamp DESC")
    List<Operation> findByOperationType(@Param("operationType") String operationType);
    
    // Count operations by product ID, operation type, and time range
    @Query("SELECT COUNT(o) FROM Operation o WHERE " +
           "(:productId IS NULL OR o.product.id = :productId) AND " +
           "o.operationType = :operationType AND " +
           "o.timestamp BETWEEN :startTime AND :endTime")
    long countByProductIdAndOperationTypeAndTimestampBetween(
            @Param("productId") Long productId,
            @Param("operationType") String operationType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // Find operations within time range
    @Query("SELECT o FROM Operation o WHERE o.timestamp BETWEEN :startTime AND :endTime ORDER BY o.timestamp DESC")
    List<Operation> findByTimestampBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // Find all operations ordered by timestamp (most recent first)
    @Query("SELECT o FROM Operation o ORDER BY o.timestamp DESC")
    List<Operation> findAllOrderByTimestampDesc();
    
    // Find all operations with pagination, ordered by timestamp
    @Query("SELECT o FROM Operation o ORDER BY o.timestamp DESC")
    Page<Operation> findAllOrderByTimestampDesc(Pageable pageable);
    
    // Search operations by product title (for operation history search functionality)
    @Query("SELECT o FROM Operation o WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(o.product.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:operationType IS NULL OR :operationType = '' OR o.operationType = :operationType) " +
           "ORDER BY o.timestamp DESC")
    Page<Operation> findOperationsWithFilters(
            @Param("search") String search,
            @Param("operationType") String operationType,
            Pageable pageable);
    
    // Delete operations by product ID (useful for cascade deletes)
    @Query("DELETE FROM Operation o WHERE o.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}