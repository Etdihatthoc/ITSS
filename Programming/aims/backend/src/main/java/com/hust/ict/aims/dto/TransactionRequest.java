// src/main/java/com/hust/ict/aims/dto/TransactionRequest.java
package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionRequest {
    // Getters and setters
    private String transactionId;
    private String bankCode;
    private float amount;
    private String cardType;
    private String payDate;
    private String errorMessage;

}