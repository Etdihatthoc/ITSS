package com.hust.ict.aims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFindAllTransactions() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(1);

        when(transactionService.findAll()).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionID").value(1));
    }

    @Test
    void testFindTransactionById_Found() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(1);

        when(transactionService.findById(1)).thenReturn(transaction);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionID").value(1));
    }

    @Test
    void testFindTransactionById_NotFound() throws Exception {
        when(transactionService.findById(999)).thenReturn(null);

        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testSaveTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setAmount(150.0f);

        when(transactionService.save(any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionID").value(1))
                .andExpect(jsonPath("$.amount").value(150.0));
    }

    @Test
    void testUpdateTransaction() throws Exception {
        Transaction updated = new Transaction();
        updated.setId(1);
        updated.setAmount(200.0f);

        when(transactionService.save(any(Transaction.class))).thenReturn(updated);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionID").value(1))
                .andExpect(jsonPath("$.amount").value(200.0));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        doNothing().when(transactionService).deleteById(1);

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk());
    }
}
