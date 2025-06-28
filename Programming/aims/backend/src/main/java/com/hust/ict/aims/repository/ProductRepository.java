package com.hust.ict.aims.repository;

import com.hust.ict.aims.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;  // <- ADD THIS LINE
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query(value = "SELECT * FROM product ORDER BY RANDOM()",
            nativeQuery = true)
    Page<Product> findAllRandom(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActive();

    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    Page<Product> findAllActive(Pageable pageable);

    @Query(value = "SELECT * FROM product WHERE deleted = false ORDER BY RANDOM()", nativeQuery = true)
    Page<Product> findAllActiveRandom(Pageable pageable);

    // Random product methods with pivot-based approach
    @Query("SELECT MIN(p.id) FROM Product p WHERE p.deleted = false")
    Long findMinId();

    @Query("SELECT MAX(p.id) FROM Product p WHERE p.deleted = false")
    Long findMaxId();

    @Query("SELECT p FROM Product p WHERE p.id >= :pivot AND p.deleted = false ORDER BY p.id ASC")
    List<Product> findFromPivot(@Param("pivot") long pivot);

    @Query("SELECT p FROM Product p WHERE p.id < :pivot AND p.deleted = false ORDER BY p.id ASC")
    List<Product> findWrapAround(@Param("pivot") long pivot);

    // New paginated random methods with seed
    @Query("SELECT COUNT(p) FROM Product p WHERE p.deleted = false")
    long countActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY p.id ASC")
    List<Product> findAllActiveOrdered();

    List<Product> findByIdIn(Set<Long> ids);
}