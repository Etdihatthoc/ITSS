package com.hust.ict.aims.subsystem.PaymentSubsystem.dto;

import com.hust.ict.aims.subsystem.PaymentSubsystem.Config.VNPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPayPayRequest extends PayRequest {
    @Autowired
    private VNPayConfig config;
    @Override
    public String getPaymentUrl(Map<String, String> params) throws UnsupportedEncodingException {
        int amount = Integer.parseInt(params.get("amount"));
        String ipAddr = params.get("ip_addr");
        String orderId = params.get("orderId");
        String orderInfo = params.get("orderInfo");
        LocalDateTime now = LocalDateTime.now();
        String vnp_CreateDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        LocalDateTime expireDate = now.plusMinutes(15); // Time for waiting user's account confirmation
        String vnp_ExpireDate = expireDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", config.getVersion());
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", config.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount*100));
        vnp_Params.put("vnp_BankCode", null);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_CurrCode", config.getCurrCode());
        vnp_Params.put("vnp_IpAddr", ipAddr);
        vnp_Params.put("vnp_Locale", config.getLocale());

        String DefaultOrderInfo = "Thanh toan hoa don " + orderId + " thoi gian " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (orderInfo == null||orderInfo.isEmpty()) {
            vnp_Params.put("vnp_OrderInfo", DefaultOrderInfo);
        } else {
            vnp_Params.put("vnp_OrderInfo", orderInfo);
        }
        vnp_Params.put("vnp_OrderType", config.getOrderType());
        vnp_Params.put("vnp_ReturnUrl", config.getPaymentResult());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = config.hmacSHA512(config.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return config.getPaymentUrl() + "?" + queryUrl;
    }}
