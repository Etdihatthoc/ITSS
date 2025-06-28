// src/main/java/com/hust/ict/aims/dto/TransactionRequest.java
package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class TransactionRequest {
    // Getters and setters
    private String transactionId;
    private float amount;
    private String payDate;
    private String gateway;
    private String transactionStatus;
    private String transactionNo;
    private Map<String, String> additionalParams;

}