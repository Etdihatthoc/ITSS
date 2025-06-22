package com.hust.ict.aims.repository;

import com.hust.ict.aims.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;  // <- ADD THIS LINE

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT * FROM product ORDER BY RANDOM()",
            nativeQuery = true)
    Page<Product> findAllRandom(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActive();

    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    Page<Product> findAllActive(Pageable pageable);

    @Query(value = "SELECT * FROM product WHERE deleted = false ORDER BY RANDOM()", nativeQuery = true)
    Page<Product> findAllActiveRandom(Pageable pageable);
}