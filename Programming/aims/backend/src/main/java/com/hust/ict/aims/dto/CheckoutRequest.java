// src/main/java/com/hust/ict/aims/dto/CheckoutRequest.java
package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.DeliveryInfo;

public class CheckoutRequest {
    private DeliveryInfo deliveryInfo;
    private InvoiceRequest invoiceData;
    private TransactionRequest transactionData;
    private String status;

    // Getters and setters
    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public InvoiceRequest getInvoiceData() {
        return invoiceData;
    }

    public void setInvoiceData(InvoiceRequest invoiceData) {
        this.invoiceData = invoiceData;
    }

    public TransactionRequest getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(TransactionRequest transactionData) {
        this.transactionData = transactionData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}