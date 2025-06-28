package com.hust.ict.aims.controller;

import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> findAll() {
        return transactionService.findAll();
    }

    @GetMapping("/{id}")
    public Transaction findById(@PathVariable int id) {
        return transactionService.findById(id);
    }

    @PostMapping
    public Transaction save(@RequestBody Transaction transaction) {
        return transactionService.save(transaction);
    }

    @PutMapping("/{id}")
    public Transaction update(@PathVariable int id, @RequestBody Transaction transaction) {
        transaction.setTransactionId(id);
        return transactionService.save(transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        transactionService.deleteById(id);
    }

}



