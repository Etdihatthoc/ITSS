package com.hust.ict.aims.subsystem.PaymentSubsystem.service;

import com.hust.ict.aims.subsystem.PaymentSubsystem.PaymentStrategyFactory;
import com.hust.ict.aims.subsystem.PaymentSubsystem.strategy.PaymentStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class PaymentService {
    @Autowired
    private PaymentStrategyFactory factory;
    public PaymentStrategy getPaymentStrategy(String gateway) {
        return factory.getStrategy(gateway);
    }
    @PostConstruct
    public void init() {
        System.out.println("✅ PaymentStrategyFactory injected: " + (factory != null));
        System.out.println("Available strategies: " + factory.getAllGateways());
    }
}