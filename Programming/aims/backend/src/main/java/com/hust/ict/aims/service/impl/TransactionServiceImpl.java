package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.dto.TransactionRequest;
import com.hust.ict.aims.repository.TransactionRepository;
import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction createTransaction(TransactionRequest transactionData) {
        Transaction transaction = new Transaction();
        transaction.setTransactionNo(transactionData.getTransactionId());
        transaction.setBankCode(transactionData.getBankCode());
        transaction.setAmount(transactionData.getAmount());
        transaction.setBankTransactionNo(transactionData.getTransactionId()); // You may want to modify this
        transaction.setCardType(transactionData.getCardType());

        // Parse payDate from string to appropriate date format
        try {
            // Parse date from ISO format (YYYY-MM-DD)
            LocalDateTime payDate = LocalDateTime.parse(transactionData.getPayDate());
            transaction.setPayDate(payDate);
        } catch (Exception e) {
            transaction.setPayDate(LocalDateTime.parse(new Date().toString())); // Default to current date if parsing fails
        }

        transaction.setErrorMessage(transactionData.getErrorMessage());

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction findById(int id) {
        Optional<Transaction> result = transactionRepository.findById(id);
        return result.orElseThrow(() -> new RuntimeException("Transaction not found - " + id));
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteById(int id) {
        transactionRepository.deleteById(id);
    }
}

