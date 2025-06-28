//package com.hust.ict.aims.subsystem.VNPaySubsystem.Refund;
//
//import com.hust.ict.aims.model.Transaction;
//import com.hust.ict.aims.subsystem.VNPaySubsystem.Config;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Service
//public class RefundRequest {
//    @Autowired
//    private Config config;
//
//    public String getRefundUrl(Transaction trans, String username, String ipAddr) throws IOException, InterruptedException {
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_RequestId", "");
//        vnp_Params.put("vnp_Version", config.getVersion());
//        vnp_Params.put("vnp_Command", "refund");
//        vnp_Params.put("vnp_TmnCode", config.getTmnCode());
//        vnp_Params.put("vnp_Amount", String.valueOf(trans.getAmount()));
//        vnp_Params.put("vnp_TransactionType", config.getTransactionType());
//        vnp_Params.put("vnp_TxnRef", trans.getTransactionNo());
//        vnp_Params.put("vnp_OrderInfo", trans.getTransactionInfo());
//        vnp_Params.put("vnp_BankCode", trans.getBankCode());
//
//        LocalDateTime payDate = trans.getPayDate();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//        String vnp_TransactionDate = payDate.format(formatter);
//        vnp_Params.put("vnp_TransactionNo", trans.getTransactionNo());
//        vnp_Params.put("vnp_TransactionDate", vnp_TransactionDate);
//        vnp_Params.put("vnp_CreateBy", username);
//
//        LocalDateTime now = LocalDateTime.now();
//        String vnp_CreateDate = now.format(formatter);
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//        vnp_Params.put("vnp_IpAddr", ipAddr);
//
//        String hashData =  vnp_Params.get("vnp_RequestId") + "|" +
//                vnp_Params.get("vnp_Version") + "|" +
//                vnp_Params.get("vnp_Command") + "|" +
//                vnp_Params.get("vnp_TmnCode") + "|" +
//                vnp_Params.get("vnp_TransactionType") + "|" +
//                vnp_Params.get("vnp_TxnRef") + "|" +
//                vnp_Params.get("vnp_Amount") + "|" +
//                vnp_Params.get("vnp_TransactionNo") + "|" +
//                vnp_Params.get("vnp_TransactionDate") + "|" +
//                vnp_Params.get("vnp_CreateBy") + "|" +
//                vnp_Params.get("vnp_CreateDate") + "|" +
//                vnp_Params.get("vnp_IpAddr") + "|" +
//                vnp_Params.get("vnp_OrderInfo");
//
////        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
////        Collections.sort(fieldNames);
////        StringBuilder hashData = new StringBuilder();
////        StringBuilder query = new StringBuilder();
////        Iterator<String> itr = fieldNames.iterator();
////
////        while (itr.hasNext()) {
////            String fieldName = (String) itr.next();
////            String fieldValue = (String) vnp_Params.get(fieldName);
////            if ((fieldValue != null) && (fieldValue.length() > 0)) {
////                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
////                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
////                query.append('=');
////                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
////                if (itr.hasNext()) {
////                    query.append('&');
////                    hashData.append('|');
////                }
////            }
////        }
//        String vnp_SecureHash = config.hmacSHA512(config.getHashSecret(), hashData.toString());
//        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
//        return sendPostRequest(config.getRefundUrl(), new JSONObject(vnp_Params).toString());
//    }
//
//    private String sendPostRequest(String url, String jsonPayload) throws IOException, InterruptedException {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        return response.body(); // Parse JSON if needed
//    }
//
////    public static void main(String[] arg) {
////        Config cfg  = new Config();
////        RefundRequest req = new RefundRequest();
////        Transaction trans = new Transaction();
//////        System.out.println(req.getRefundUrl());
////    }
//
//}
