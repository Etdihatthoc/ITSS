//package com.hust.ict.aims.subsystem.VNPaySubsystem.Payment;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.hust.ict.aims.exception.PaymentException;
//import com.hust.ict.aims.model.Transaction;
//import com.hust.ict.aims.subsystem.VNPaySubsystem.Config;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class PayResponse {
//    @Autowired
//    private Config config;
//
//    public Transaction createTransaction(Map<String, String> params) throws PaymentException {
//        Transaction currentTrans = new Transaction();
//
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            handleParams(currentTrans, entry.getKey(), entry.getValue());
//        }
//        return currentTrans;
//    }
//
//    private void handleParams(Transaction currentTrans, String key, String value) throws PaymentException {
//        switch (key) {
//            case "vnp_Amount":
//                currentTrans.setAmount(Float.parseFloat(value)/100);
//                break;
//
//            case "vnp_BankCode":
//                currentTrans.setBankCode(value);
//                break;
//
//            case "vnp_BankTranNo":
//                currentTrans.setBankTransactionNo(value);
//                break;
//
//            case "vnp_CardType":
//                currentTrans.setCardType(value);
//                break;
//
//            case "vnp_OrderInfo":
//                currentTrans.setTransactionInfo(value);
//                break;
//
//            case "vnp_PayDate":
//                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//                LocalDateTime dateTime = LocalDateTime.parse(value, inputFormatter);
//                currentTrans.setPayDate(dateTime);
////  !MUST! : Convert payDate from LocalDate to LocalDateTime in database
////                LocalDate formattedDate = dateTime.toLocalDate();
////                currentTrans.setPayDate(formattedDate);
//                break;
//
//            case "vnp_ResponseCode":
//                String message = config.getErrorMessage(value);
//                currentTrans.setErrorMessage(message);
//                config.handleErrorCode(value, message);
//                break;
//
//            case "vnp_TransactionNo":
//                currentTrans.setTransactionNo(value);
//                break;
//
//            case "vnp_TransactionStatus":
//                /*
//                if (!value.equals("00")) {
//                    System.out.println("TransactionStatus not success (" + value + ")");
//                }
//                */
//                break;
//            case "vnp_TmnCode":
//                break;
//            case "vnp_TxnRef":
//                break;
//            case "vnp_SecureHash":
//                break;
//            default:
//                System.err.println("Unrecognized param: " + key);
//                break;
//        }
//    }
//    /*
//    vnp_Amount: 120000000
//    vnp_BankCode: NCB
//    vnp_BankTranNo: VNP15007230
//    vnp_CardType: ATM
//    vnp_OrderInfo: Thanh toan hoa don 12414 thoi gian 2025-06-08 17:01:05
//    vnp_PayDate: 20250608170219
//    vnp_ResponseCode: 00
//    vnp_TmnCode: YGQ5EWCK
//    vnp_TransactionNo: 15007230
//    vnp_TransactionStatus: 00
//    vnp_TxnRef: 12414
//    vnp_SecureHash: ed78098486bcbe52eb873ae25855ace3adb4182bf26d66e69638d0dab2db59899b84c0f5da99c0be44c8abd80720cc61922e3c0746afce8177d64a084cb854ed
//    */
//    public void handleSuccessfulParams(Map<String, String> successfulParams, String key, String value) {
//        switch (key) {
//            case "vnp_Amount":
//                successfulParams.put(key, value);
//                break;
//
//            case "vnp_BankCode":
//                successfulParams.put(key, value);
//                break;
//
//            case "vnp_BankTranNo":
//                successfulParams.put(key, value);
//                break;
//
//            case "vnp_CardType":
//                successfulParams.put(key, value);
//                break;
//
//            case "vnp_OrderInfo":
//                successfulParams.put(key, value);
//                break;
//
//            case "vnp_PayDate":
//                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//                LocalDateTime dateTime = LocalDateTime.parse(value, inputFormatter);
//                DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                String payDate = dateTime.format(formatedDate);
//                successfulParams.put(key, payDate);
//                break;
//
//            case "vnp_ResponseCode":
//                break;
//
//            case "vnp_TransactionNo":
//                successfulParams.put(key, value);
//                break;
//
//            case "vnp_TransactionStatus":
//                /*
//                if (!value.equals("00")) {
//                    System.out.println("TransactionStatus not success (" + value + ")");
//                }
//                */
//                break;
//            case "vnp_TmnCode":
//                break;
//            case "vnp_TxnRef":
//                break;
//            case "vnp_SecureHash":
//                break;
//            default:
//                System.err.println("Unrecognized param: " + key);
//                break;
//        }
//    }
//
//    // Get successful transaction info
//    public Map<String, String> successfulTransactionInfo(Map<String, String> allParams) {
//        Map<String, String> transaction_info=new HashMap<>();
//        for (Map.Entry<String, String> entry : allParams.entrySet()) {
//            handleSuccessfulParams(transaction_info, entry.getKey(), entry.getValue());
//        }
//        return transaction_info;
//    }
//    // Get fail transaction info
//
//}
