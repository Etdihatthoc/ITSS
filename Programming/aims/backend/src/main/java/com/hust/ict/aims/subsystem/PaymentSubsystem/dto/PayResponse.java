package com.hust.ict.aims.subsystem.PaymentSubsystem.dto;

import com.hust.ict.aims.exception.PaymentException;
import com.hust.ict.aims.model.Transaction;

import java.util.Map;

public abstract class PayResponse {
    public abstract Transaction createTransaction(Map<String, String> requestParams) throws PaymentException;
}