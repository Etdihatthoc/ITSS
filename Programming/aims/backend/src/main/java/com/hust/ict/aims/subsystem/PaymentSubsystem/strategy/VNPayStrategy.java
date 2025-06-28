package com.hust.ict.aims.subsystem.PaymentSubsystem.strategy;

import com.hust.ict.aims.exception.PaymentException;
import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.subsystem.PaymentSubsystem.dto.VNPayPayRequest;
import com.hust.ict.aims.subsystem.PaymentSubsystem.dto.VNPayPayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service("VNPay")
public class VNPayStrategy implements PaymentStrategy {
    @Autowired
    private VNPayPayRequest vnPayPayRequest;
    @Autowired
    private VNPayPayResponse vnPayPayResponse;

    public String createPaymentRequest(Map<String, String> parameters) throws UnsupportedEncodingException {
        return vnPayPayRequest.getPaymentUrl(parameters);
    }
    public Transaction createPaymentTransaction(Map<String, String> parameters) throws PaymentException {
        return vnPayPayResponse.createTransaction(parameters);
    }
}
