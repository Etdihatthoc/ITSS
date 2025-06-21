package com.hust.ict.aims.subsystem.PaymentSubsystem.controller;

import com.hust.ict.aims.subsystem.PaymentSubsystem.PaymentStrategyFactory;
import com.hust.ict.aims.subsystem.PaymentSubsystem.service.PaymentService;
import com.hust.ict.aims.subsystem.PaymentSubsystem.strategy.PaymentStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @GetMapping("/api/pay_test")
    public void submitOrder(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws UnsupportedEncodingException, IOException {
        Map<String, String> requestParams = getRequestParams(request);
        String ipAddr = request.getRemoteAddr();
        requestParams.put("ip_addr", ipAddr);
        String gateway = requestParams.get("gateway");
        PaymentStrategy paymentStrategy = paymentService.getPaymentStrategy(gateway);
        String paymentUrl = paymentStrategy.createPaymentRequest(requestParams);
        System.out.println("######## paymentUrl ######### " + paymentUrl);
        response.sendRedirect(paymentUrl);
    }

    public Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                paramMap.put(key, values[0]); // take the first value if multiple
            }
        });
        return paramMap;
    }

}
