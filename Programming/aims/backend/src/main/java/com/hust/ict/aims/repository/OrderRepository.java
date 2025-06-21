package com.hust.ict.aims.repository;

import com.hust.ict.aims.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
