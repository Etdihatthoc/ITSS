package com.hust.ict.aims.repository;

import com.hust.ict.aims.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
