package com.hust.ict.aims.subsystem.PaymentSubsystem;

import com.hust.ict.aims.subsystem.PaymentSubsystem.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class PaymentStrategyFactory {
    private final Map<String, PaymentStrategy> strategies = new HashMap<>();
    @Autowired
    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        for (PaymentStrategy strategy : strategyList) {
            System.out.println("### -> ###" + strategy.getClass().getSimpleName());
            Service annotation = strategy.getClass().getAnnotation(Service.class);
            if (annotation != null) {
                strategies.put(annotation.value().toUpperCase(), strategy);
            }
        }
    }
    public PaymentStrategy getStrategy(String gateway) {
        PaymentStrategy strategy = strategies.get(gateway.toUpperCase());
        if (strategy == null) throw new IllegalArgumentException("Unsupported gateway: " + gateway);
        return strategy;
    }
    public Set<String> getAllGateways() {
        return strategies.keySet();
    }
}
