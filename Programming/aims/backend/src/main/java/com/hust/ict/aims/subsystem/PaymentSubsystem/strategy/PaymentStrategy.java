package com.hust.ict.aims.subsystem.PaymentSubsystem.strategy;

import com.hust.ict.aims.model.Transaction;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
public interface PaymentStrategy {
    public String createPaymentRequest(Map<String, String> parameters) throws UnsupportedEncodingException;
    public Transaction createPaymentTransaction(Map<String, String> parameters);
}
