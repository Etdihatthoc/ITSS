//package com.hust.ict.aims.subsystem.VNPaySubsystem;
//import com.hust.ict.aims.exception.PaymentException;
//import com.hust.ict.aims.model.Transaction;
//import com.hust.ict.aims.subsystem.VNPaySubsystem.Payment.PayRequest;
//import com.hust.ict.aims.subsystem.VNPaySubsystem.Payment.PayResponse;
//import com.hust.ict.aims.subsystem.VNPaySubsystem.Refund.RefundRequest;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.HashMap;
//import java.util.Map;
//@CrossOrigin(origins = "http://localhost:5173")
//@Controller
//public class VNPayController {
//    @Autowired
//    private PayRequest pay_request;
//    @Autowired
//    private PayResponse pay_response;
//
//    @Autowired
//    private RefundRequest refund_request;
////    @GetMapping("/invoice")
////    public String invoicePage() {
////        return "invoice";
////    }
//
////    @PostMapping("/api/pay")
////    public String submitOrder(
////            HttpServletRequest req
////    ) throws UnsupportedEncodingException {
////        int amount = Integer.parseInt(req.getParameter("amount"));
////        long orderId = Long.parseLong(req.getParameter("orderId")); //orderId
////        String orderInfo = req.getParameter("orderInfo"); //orderInfo
////        String clientIpAddr = req.getRemoteAddr();
////        String redirectUrl = pay_request.getPaymentUrl(amount, orderId, orderInfo, clientIpAddr);
////        return "redirect:" + redirectUrl;
////    }
//
//    @GetMapping("/api/pay")
//    public String submitOrder(
//            @RequestParam("amount") String amountStr,
//            @RequestParam("orderId") String orderIdStr,
//            @RequestParam(value = "orderInfo", required = false, defaultValue = "") String orderInfo,
//            HttpServletRequest request
//    ) throws UnsupportedEncodingException {
//        int amount = Integer.parseInt(amountStr);
//        long orderId = Long.parseLong(orderIdStr);
//        String clientIpAddr = request.getRemoteAddr();
//        String redirectUrl = pay_request.getPaymentUrl(amount, orderId, orderInfo, clientIpAddr);
//        return "redirect:" + redirectUrl;
//    }
//
//    @GetMapping("/payment-result")
//    public String paymentReturn(@RequestParam Map<String,String> allParams, Model model, HttpSession session, HttpServletRequest req) {
//        Map<String, String> transaction_info=new HashMap<>();
//        try{
//            Transaction transaction = pay_response.createTransaction(allParams);
//            transaction_info = pay_response.successfulTransactionInfo(allParams);
//            session.setAttribute("transaction_info", transaction);
////            String refund_url = refund_request.getRefundUrl(transaction, "AIMS", req.getRemoteAddr());
////            session.setAttribute("refund_url", refund_url);
//            model.addAttribute("params", transaction_info);
//            return "successful_payment";
//        } catch (PaymentException e) {
//            String errorMessage = e.getMessage();
//            System.err.println(errorMessage);
//            transaction_info.put("ERROR", errorMessage);
//            model.addAttribute("params", transaction_info);
//            return "fail_payment";
//        }
//    }
//
//
//}
