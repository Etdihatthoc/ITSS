package com.hust.ict.aims.subsystem.PaymentSubsystem.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.hust.ict.aims.exception.PaymentException;
import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.subsystem.PaymentSubsystem.Config.VNPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VNPayPayResponse extends PayResponse {
    @Autowired
    private VNPayConfig config;

    public Transaction createTransaction(Map<String, String> params) throws PaymentException {
        Transaction currentTrans = new Transaction();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            handleParams(currentTrans, entry.getKey(), entry.getValue());
        }
        return currentTrans;
    }

    private void handleParams(Transaction currentTrans, String key, String value) throws PaymentException {
        switch (key) {
            case "gateway":
                currentTrans.setGateway(value);
                break;

            case "vnp_Amount":
                currentTrans.setAmount(Float.parseFloat(value)/100);
                break;

            case "vnp_BankCode":
            case "vnp_BankTranNo":
            case "vnp_CardType":
            case "vnp_OrderInfo":
                currentTrans.getAdditionalParams().put(key, value);
                break;

            case "vnp_PayDate":
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime dateTime = LocalDateTime.parse(value, inputFormatter);
                currentTrans.setPayDate(dateTime);
                break;

            case "vnp_ResponseCode":
                String message = config.getErrorMessage(value);
                config.handleErrorCode(value, message);
                currentTrans.setTransactionStatus("PENDING");
                break;

            case "vnp_TransactionNo":
                currentTrans.setTransactionNo(value);
                break;

            case "vnp_TransactionStatus":
                break;
            case "vnp_TmnCode":
                break;
            case "vnp_TxnRef":
                break;
            case "vnp_SecureHash":
                break;
            default:
                System.err.println("Unrecognized param: " + key);
                break;
        }
    }
}
