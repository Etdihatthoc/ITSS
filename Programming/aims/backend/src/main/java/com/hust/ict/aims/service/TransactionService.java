package com.hust.ict.aims.service;

import com.hust.ict.aims.dto.TransactionRequest;
import com.hust.ict.aims.model.Transaction;
import java.util.List;

public interface TransactionService {
    List<Transaction> findAll();
    Transaction findById(int id);
    Transaction save(Transaction transaction);
    void deleteById(int id);
    Transaction createTransaction(TransactionRequest transactionData);
}
