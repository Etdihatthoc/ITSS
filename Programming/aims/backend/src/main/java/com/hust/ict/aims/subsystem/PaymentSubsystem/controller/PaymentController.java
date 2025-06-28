package com.hust.ict.aims.subsystem.PaymentSubsystem.controller;

import com.hust.ict.aims.dto.TransactionRequest;
import com.hust.ict.aims.exception.PaymentException;
import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.service.TransactionService;
import com.hust.ict.aims.service.impl.TransactionServiceImpl;
import com.hust.ict.aims.subsystem.IPayment;
import com.hust.ict.aims.subsystem.PaymentSubsystem.service.PaymentService;
import com.hust.ict.aims.subsystem.PaymentSubsystem.strategy.PaymentStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
@Component
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController implements IPayment {
    @Autowired
    private PaymentService paymentService;

    @Value("${checkout.confirmation}")
    private String checkoutConfirmation;

    @Autowired
    private TransactionServiceImpl transactionService;

    @GetMapping("/api/pay_test")
    public void submitOrder(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ) throws UnsupportedEncodingException, IOException {
        Map<String, String> requestParams = getRequestParams(request);
        String ipAddr = request.getRemoteAddr();
        requestParams.put("ip_addr", ipAddr);
        String gateway = requestParams.get("gateway");
        session.setAttribute("gateway", gateway);
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

    @GetMapping("/payment-result")
    public void paymentReturn(@RequestParam Map<String,String> allParams, HttpServletResponse response, HttpSession session) throws IOException {
        String gateway = (String) session.getAttribute("gateway");
        session.removeAttribute("gateway");
        allParams.put("gateway", gateway);
        PaymentStrategy  paymentStrategy = paymentService.getPaymentStrategy(gateway);
        try {
            Transaction transaction = paymentStrategy.createPaymentTransaction(allParams);
            payOrder(transaction);
            response.sendRedirect(this.checkoutConfirmation + "?transactionId=" + transaction.getTransactionId());
        } catch (PaymentException e) {
            System.err.println(e.getMessage());
            response.sendRedirect(this.checkoutConfirmation + "?transactionId=-1");
        }
    }

    @Override
    public void payOrder(Transaction trans) {
        transactionService.createTransaction(trans);
    }

}
