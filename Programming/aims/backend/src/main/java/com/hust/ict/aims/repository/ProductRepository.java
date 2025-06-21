package com.hust.ict.aims.repository;

import com.hust.ict.aims.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT * FROM product ORDER BY RANDOM()",
            nativeQuery = true)
    Page<Product> findAllRandom(Pageable pageable);
}
